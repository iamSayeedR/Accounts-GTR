package com.example.accounts.service;

import com.example.accounts.dto.InvoiceLineRequest;
import com.example.accounts.dto.InvoiceRequest;
import com.example.accounts.dto.InvoiceResponse;
import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.*;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.InvalidTransactionException;
import com.example.accounts.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private ChartOfAccountRepository chartOfAccountRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Company customer;
    private Item item;
    private ChartOfAccount arAccount;
    private ChartOfAccount revenueAccount;
    private ChartOfAccount vatAccount;
    private InvoiceRequest customerInvoiceRequest;

    @BeforeEach
    void setUp() {
        // Setup GL Accounts
        arAccount = ChartOfAccount.builder()
                .accountId(1L)
                .accountCode("1100")
                .description("Accounts Receivable")
                .accountType(AccountBalanceType.DEBIT)
                .build();

        revenueAccount = ChartOfAccount.builder()
                .accountId(2L)
                .accountCode("4000")
                .description("Sales Revenue")
                .accountType(AccountBalanceType.CREDIT)
                .build();

        vatAccount = ChartOfAccount.builder()
                .accountId(3L)
                .accountCode("2200")
                .description("Output VAT")
                .accountType(AccountBalanceType.CREDIT)
                .build();

        // Setup Company with GL Accounts
        CompanyGLAccount companyGL = CompanyGLAccount.builder()
                .accountsReceivable(arAccount)
                .build();

        customer = Company.builder()
                .companyId(1L)
                .code("CUST001")
                .name("Customer ABC")
                .companyType(CompanyType.CUSTOMER)
                .companyGLAccount(companyGL)
                .build();

        // Setup Item with GL Accounts
        ItemGLAccount itemGL = ItemGLAccount.builder()
                .salesRevenueAccount(revenueAccount)
                .outputVATAccount(vatAccount)
                .build();

        item = Item.builder()
                .itemId(1L)
                .code("ITEM001")
                .description("Product A")
                .itemType(ItemType.INVENTORY_ITEM)
                .itemGLAccount(itemGL)
                .build();

        // Setup Invoice Request
        InvoiceLineRequest lineRequest = InvoiceLineRequest.builder()
                .lineNumber(1)
                .itemId(1L)
                .quantity(new BigDecimal("10"))
                .unitPrice(new BigDecimal("100.00"))
                .vatRate(new BigDecimal("15"))
                .discountRate(BigDecimal.ZERO)
                .build();

        customerInvoiceRequest = InvoiceRequest.builder()
                .invoiceNumber("INV-001")
                .invoiceDate(LocalDate.now())
                .documentType(DocumentType.CUSTOMER_INVOICE)
                .companyId(1L)
                .lines(Arrays.asList(lineRequest))
                .build();
    }

    @Test
    void createInvoice_Success() {
        when(invoiceRepository.existsByInvoiceNumber("INV-001")).thenReturn(false);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice inv = invocation.getArgument(0);
            inv.setInvoiceId(1L);
            return inv;
        });

        InvoiceResponse response = invoiceService.createInvoice(customerInvoiceRequest);

        assertNotNull(response);
        assertEquals("INV-001", response.getInvoiceNumber());
        assertEquals(1, response.getLines().size());

        // Verify calculations
        assertEquals(new BigDecimal("1000.00"), response.getSubtotalAmount());
        assertEquals(new BigDecimal("150.00"), response.getVatAmount());
        assertEquals(new BigDecimal("1150.00"), response.getTotalAmount());

        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void createInvoice_DuplicateNumber_ThrowsException() {
        when(invoiceRepository.existsByInvoiceNumber("INV-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            invoiceService.createInvoice(customerInvoiceRequest);
        });

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void postInvoice_CustomerInvoice_CreatesCorrectJournalEntry() {
        // Setup invoice
        InvoiceLine line = InvoiceLine.builder()
                .lineId(1L)
                .lineNumber(1)
                .item(item)
                .quantity(new BigDecimal("10"))
                .unitPrice(new BigDecimal("100.00"))
                .vatRate(new BigDecimal("15"))
                .discountRate(BigDecimal.ZERO)
                .build();
        line.calculateAmounts();

        Invoice invoice = Invoice.builder()
                .invoiceId(1L)
                .invoiceNumber("INV-001")
                .invoiceDate(LocalDate.now())
                .documentType(DocumentType.CUSTOMER_INVOICE)
                .company(customer)
                .isPosted(false)
                .build();
        invoice.addLine(line);
        invoice.calculateTotals();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(journalEntryRepository.save(any(JournalEntry.class))).thenAnswer(invocation -> {
            JournalEntry je = invocation.getArgument(0);
            je.setJournalEntryId(1L);
            return je;
        });
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponse response = invoiceService.postInvoice(1L, "admin");

        assertNotNull(response);
        assertTrue(response.getIsPosted());
        assertNotNull(response.getJournalEntryId());
        assertEquals(InvoiceStatus.POSTED, response.getStatus());

        // Verify journal entry was created
        verify(journalEntryRepository, times(1)).save(any(JournalEntry.class));
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void postInvoice_AlreadyPosted_ThrowsException() {
        Invoice postedInvoice = Invoice.builder()
                .invoiceId(1L)
                .isPosted(true)
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(postedInvoice));

        assertThrows(InvalidTransactionException.class, () -> {
            invoiceService.postInvoice(1L, "admin");
        });

        verify(journalEntryRepository, never()).save(any(JournalEntry.class));
    }

    @Test
    void postInvoice_NoCompanyGLAccounts_ThrowsException() {
        customer.setCompanyGLAccount(null);

        InvoiceLine line = InvoiceLine.builder()
                .item(item)
                .quantity(new BigDecimal("10"))
                .unitPrice(new BigDecimal("100.00"))
                .build();

        Invoice invoice = Invoice.builder()
                .invoiceId(1L)
                .documentType(DocumentType.CUSTOMER_INVOICE)
                .company(customer)
                .isPosted(false)
                .build();
        invoice.addLine(line);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(InvalidTransactionException.class, () -> {
            invoiceService.postInvoice(1L, "admin");
        });
    }

    @Test
    void postInvoice_NoItemGLAccounts_ThrowsException() {
        item.setItemGLAccount(null);

        InvoiceLine line = InvoiceLine.builder()
                .item(item)
                .quantity(new BigDecimal("10"))
                .unitPrice(new BigDecimal("100.00"))
                .build();

        Invoice invoice = Invoice.builder()
                .invoiceId(1L)
                .documentType(DocumentType.CUSTOMER_INVOICE)
                .company(customer)
                .isPosted(false)
                .build();
        invoice.addLine(line);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(InvalidTransactionException.class, () -> {
            invoiceService.postInvoice(1L, "admin");
        });
    }

    @Test
    void invoiceLineCalculations_WithVATAndDiscount() {
        InvoiceLine line = InvoiceLine.builder()
                .quantity(new BigDecimal("10"))
                .unitPrice(new BigDecimal("100.00"))
                .vatRate(new BigDecimal("15"))
                .discountRate(new BigDecimal("10"))
                .build();

        line.calculateAmounts();

        // Line Amount = 10 * 100 = 1000
        assertEquals(new BigDecimal("1000.00"), line.getLineAmount());

        // Discount = 1000 * 10% = 100
        assertEquals(new BigDecimal("100.00"), line.getDiscountAmount());

        // Amount after discount = 1000 - 100 = 900
        // VAT = 900 * 15% = 135
        assertEquals(new BigDecimal("135.00"), line.getVatAmount());

        // Net = 900 + 135 = 1035
        assertEquals(new BigDecimal("1035.00"), line.getNetAmount());
    }
}
