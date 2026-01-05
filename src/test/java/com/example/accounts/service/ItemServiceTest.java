package com.example.accounts.service;

import com.example.accounts.dto.ItemGLAccountRequest;
import com.example.accounts.dto.ItemRequest;
import com.example.accounts.dto.ItemResponse;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.entity.Item;
import com.example.accounts.entity.ItemGLAccount;
import com.example.accounts.entity.enums.ItemType;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ChartOfAccountRepository;
import com.example.accounts.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ChartOfAccountRepository chartOfAccountRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private ItemRequest testRequest;
    private ChartOfAccount revenueAccount;

    @BeforeEach
    void setUp() {
        revenueAccount = ChartOfAccount.builder()
                .accountId(1L)
                .accountCode("4000")
                .description("Sales Revenue")
                .build();

        ItemGLAccount glAccount = ItemGLAccount.builder()
                .salesRevenueAccount(revenueAccount)
                .build();

        testItem = Item.builder()
                .itemId(1L)
                .code("ITEM001")
                .description("Product A")
                .itemType(ItemType.INVENTORY_ITEM)
                .isActive(true)
                .itemGLAccount(glAccount)
                .build();

        ItemGLAccountRequest glRequest = ItemGLAccountRequest.builder()
                .salesRevenueAccountId(1L)
                .build();

        testRequest = ItemRequest.builder()
                .code("ITEM001")
                .description("Product A")
                .itemType(ItemType.INVENTORY_ITEM)
                .glAccounts(glRequest)
                .build();
    }

    @Test
    void createItem_Success() {
        when(itemRepository.existsByCode("ITEM001")).thenReturn(false);
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(revenueAccount));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemResponse response = itemService.createItem(testRequest);

        assertNotNull(response);
        assertEquals("ITEM001", response.getCode());
        assertEquals("Product A", response.getDescription());
        assertNotNull(response.getGlAccounts());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_DuplicateCode_ThrowsException() {
        when(itemRepository.existsByCode("ITEM001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            itemService.createItem(testRequest);
        });

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemById_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        ItemResponse response = itemService.getItemById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getItemId());
        assertEquals("ITEM001", response.getCode());
    }

    @Test
    void activateItem_Success() {
        testItem.setIsActive(false);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemResponse response = itemService.activateItem(1L);

        assertTrue(response.getIsActive());
    }
}
