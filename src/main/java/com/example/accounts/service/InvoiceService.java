package com.example.accounts.service;

import com.example.accounts.dto.*;
import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.DocumentType;
import com.example.accounts.entity.enums.InvoiceStatus;
import com.example.accounts.entity.enums.JournalEntryStatus;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.InvalidTransactionException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final ItemRepository itemRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    public InvoiceResponse createInvoice(InvoiceRequest request) {
        if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
            throw new DuplicateResourceException("Invoice number already exists: " + request.getInvoiceNumber());
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        Invoice invoice = Invoice.builder()
                .invoiceNumber(request.getInvoiceNumber())
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .documentType(request.getDocumentType())
                .company(company)
                .contract(request.getContract())
                .entity(request.getEntity())
                .warehouse(request.getWarehouse())
                .basis(request.getBasis())
                .notes(request.getNotes())
                .status(InvoiceStatus.DRAFT)
                .isPosted(false)
                .build();

        // Add lines
        for (InvoiceLineRequest lineReq : request.getLines()) {
            InvoiceLine line = createInvoiceLine(lineReq);
            invoice.addLine(line);
        }

        // Calculate totals
        invoice.calculateTotals();

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return mapToResponse(invoice);
    }

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> getInvoicesByCompany(Long companyId) {
        return invoiceRepository.findByCompanyCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public InvoiceResponse postInvoice(Long id, String postedBy) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getIsPosted()) {
            throw new InvalidTransactionException("Invoice is already posted");
        }

        // Create automatic journal entry
        JournalEntry journalEntry = createJournalEntryForInvoice(invoice, postedBy);
        JournalEntry savedJE = journalEntryRepository.save(journalEntry);

        invoice.setJournalEntry(savedJE);
        invoice.setIsPosted(true);
        invoice.setPostedDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.POSTED);

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    private JournalEntry createJournalEntryForInvoice(Invoice invoice, String postedBy) {
        JournalEntry je = JournalEntry.builder()
                .entryNumber("JE-INV-" + invoice.getInvoiceNumber())
                .entryDate(invoice.getInvoiceDate())
                .documentType(invoice.getDocumentType())
                .description("Auto-posting for Invoice: " + invoice.getInvoiceNumber())
                .company(invoice.getCompany())
                .referenceNumber(invoice.getInvoiceNumber())
                .status(JournalEntryStatus.POSTED)
                .postedDate(LocalDateTime.now())
                .postedBy(postedBy)
                .lines(new ArrayList<>())
                .build();

        boolean isCustomerInvoice = invoice.getDocumentType() == DocumentType.CUSTOMER_INVOICE;
        Company company = invoice.getCompany();

        if (company.getCompanyGLAccount() == null) {
            throw new InvalidTransactionException("Company GL accounts not configured for: " + company.getName());
        }

        int lineNumber = 1;

        // For each invoice line, create GL postings
        for (InvoiceLine invLine : invoice.getLines()) {
            Item item = invLine.getItem();

            if (item.getItemGLAccount() == null) {
                throw new InvalidTransactionException("Item GL accounts not configured for: " + item.getDescription());
            }

            if (isCustomerInvoice) {
                // Customer Invoice Posting:
                // DR: Accounts Receivable (Company GL)
                // CR: Sales Revenue (Item GL)
                // CR: Output VAT (Item GL)

                // Revenue line (Credit)
                JournalEntryLine revenueLine = JournalEntryLine.builder()
                        .lineNumber(lineNumber++)
                        .account(item.getItemGLAccount().getSalesRevenueAccount())
                        .debitAmount(BigDecimal.ZERO)
                        .creditAmount(invLine.getLineAmount().subtract(invLine.getDiscountAmount()))
                        .description("Sales - " + item.getDescription())
                        .item(item)
                        .company(company)
                        .warehouse(invoice.getWarehouse())
                        .contract(invoice.getContract())
                        .quantity(invLine.getQuantity())
                        .build();
                je.getLines().add(revenueLine);
                revenueLine.setJournalEntry(je);

                // VAT line (Credit) if applicable
                if (invLine.getVatAmount().compareTo(BigDecimal.ZERO) > 0) {
                    JournalEntryLine vatLine = JournalEntryLine.builder()
                            .lineNumber(lineNumber++)
                            .account(item.getItemGLAccount().getOutputVATAccount())
                            .debitAmount(BigDecimal.ZERO)
                            .creditAmount(invLine.getVatAmount())
                            .description("Output VAT - " + item.getDescription())
                            .item(item)
                            .company(company)
                            .build();
                    je.getLines().add(vatLine);
                    vatLine.setJournalEntry(je);
                }

                // Discount line (Debit) if applicable
                if (invLine.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0 &&
                        item.getItemGLAccount().getTradeDiscountsAccount() != null) {
                    JournalEntryLine discountLine = JournalEntryLine.builder()
                            .lineNumber(lineNumber++)
                            .account(item.getItemGLAccount().getTradeDiscountsAccount())
                            .debitAmount(invLine.getDiscountAmount())
                            .creditAmount(BigDecimal.ZERO)
                            .description("Trade Discount - " + item.getDescription())
                            .item(item)
                            .company(company)
                            .build();
                    je.getLines().add(discountLine);
                    discountLine.setJournalEntry(je);
                }

            } else {
                // Supplier Invoice Posting:
                // DR: Expense/COGS/Asset (Item GL)
                // DR: Input VAT (Item GL)
                // CR: Accounts Payable (Company GL)

                // Expense/COGS line (Debit)
                ChartOfAccount expenseAccount = item.getItemGLAccount().getGlAccount() != null
                        ? item.getItemGLAccount().getGlAccount()
                        : item.getItemGLAccount().getCostOfGoodsSoldAccount();

                if (expenseAccount == null) {
                    throw new InvalidTransactionException(
                            "No expense/COGS account configured for item: " + item.getDescription());
                }

                JournalEntryLine expenseLine = JournalEntryLine.builder()
                        .lineNumber(lineNumber++)
                        .account(expenseAccount)
                        .debitAmount(invLine.getLineAmount().subtract(invLine.getDiscountAmount()))
                        .creditAmount(BigDecimal.ZERO)
                        .description("Purchase - " + item.getDescription())
                        .item(item)
                        .company(company)
                        .warehouse(invoice.getWarehouse())
                        .contract(invoice.getContract())
                        .quantity(invLine.getQuantity())
                        .build();
                je.getLines().add(expenseLine);
                expenseLine.setJournalEntry(je);

                // VAT line (Debit) if applicable
                if (invLine.getVatAmount().compareTo(BigDecimal.ZERO) > 0) {
                    JournalEntryLine vatLine = JournalEntryLine.builder()
                            .lineNumber(lineNumber++)
                            .account(item.getItemGLAccount().getInputVATAccount())
                            .debitAmount(invLine.getVatAmount())
                            .creditAmount(BigDecimal.ZERO)
                            .description("Input VAT - " + item.getDescription())
                            .item(item)
                            .company(company)
                            .build();
                    je.getLines().add(vatLine);
                    vatLine.setJournalEntry(je);
                }
            }
        }

        // AR/AP line (balancing entry)
        if (isCustomerInvoice) {
            // Debit Accounts Receivable
            JournalEntryLine arLine = JournalEntryLine.builder()
                    .lineNumber(lineNumber)
                    .account(company.getCompanyGLAccount().getAccountsReceivable())
                    .debitAmount(invoice.getTotalAmount())
                    .creditAmount(BigDecimal.ZERO)
                    .description("AR - Invoice " + invoice.getInvoiceNumber())
                    .company(company)
                    .build();
            je.getLines().add(arLine);
            arLine.setJournalEntry(je);
        } else {
            // Credit Accounts Payable
            JournalEntryLine apLine = JournalEntryLine.builder()
                    .lineNumber(lineNumber)
                    .account(company.getCompanyGLAccount().getAccountsPayable())
                    .debitAmount(BigDecimal.ZERO)
                    .creditAmount(invoice.getTotalAmount())
                    .description("AP - Invoice " + invoice.getInvoiceNumber())
                    .company(company)
                    .build();
            je.getLines().add(apLine);
            apLine.setJournalEntry(je);
        }

        // Calculate totals
        je.calculateTotals();

        // Validate balance
        if (!je.isBalanced()) {
            throw new InvalidTransactionException("Generated journal entry is not balanced. Debit: " +
                    je.getTotalDebit() + ", Credit: " + je.getTotalCredit());
        }

        return je;
    }

    private InvoiceLine createInvoiceLine(InvoiceLineRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        InvoiceLine line = InvoiceLine.builder()
                .lineNumber(request.getLineNumber())
                .item(item)
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .vatRate(request.getVatRate() != null ? request.getVatRate() : BigDecimal.ZERO)
                .discountRate(request.getDiscountRate() != null ? request.getDiscountRate() : BigDecimal.ZERO)
                .build();

        line.calculateAmounts();
        return line;
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceLineResponse> lineResponses = invoice.getLines().stream()
                .map(this::mapLineToResponse)
                .collect(Collectors.toList());

        return InvoiceResponse.builder()
                .invoiceId(invoice.getInvoiceId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .documentType(invoice.getDocumentType())
                .companyId(invoice.getCompany().getCompanyId())
                .companyCode(invoice.getCompany().getCode())
                .companyName(invoice.getCompany().getName())
                .contract(invoice.getContract())
                .entity(invoice.getEntity())
                .warehouse(invoice.getWarehouse())
                .basis(invoice.getBasis())
                .subtotalAmount(invoice.getSubtotalAmount())
                .vatAmount(invoice.getVatAmount())
                .discountAmount(invoice.getDiscountAmount())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .balanceAmount(invoice.getBalanceAmount())
                .status(invoice.getStatus())
                .isPosted(invoice.getIsPosted())
                .postedDate(invoice.getPostedDate())
                .journalEntryId(
                        invoice.getJournalEntry() != null ? invoice.getJournalEntry().getJournalEntryId() : null)
                .journalEntryNumber(
                        invoice.getJournalEntry() != null ? invoice.getJournalEntry().getEntryNumber() : null)
                .lines(lineResponses)
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .createdBy(invoice.getCreatedBy())
                .build();
    }

    private InvoiceLineResponse mapLineToResponse(InvoiceLine line) {
        return InvoiceLineResponse.builder()
                .lineId(line.getLineId())
                .lineNumber(line.getLineNumber())
                .itemId(line.getItem().getItemId())
                .itemCode(line.getItem().getCode())
                .itemDescription(line.getItem().getDescription())
                .description(line.getDescription())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .lineAmount(line.getLineAmount())
                .vatRate(line.getVatRate())
                .vatAmount(line.getVatAmount())
                .discountRate(line.getDiscountRate())
                .discountAmount(line.getDiscountAmount())
                .netAmount(line.getNetAmount())
                .build();
    }
}
