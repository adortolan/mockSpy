package com.devsuperior.examplemockspy.service;

import com.devsuperior.examplemockspy.dto.ProductDTO;
import com.devsuperior.examplemockspy.entities.Product;
import com.devsuperior.examplemockspy.repositories.ProductRepository;
import com.devsuperior.examplemockspy.services.ProductService;
import com.devsuperior.examplemockspy.services.exceptions.InvalidDataException;
import com.devsuperior.examplemockspy.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private ProductDTO dto;

    @BeforeEach
    void setup() {
        dto = new ProductDTO(null, "Teste", 10.0);
    }

    @Test
    void testInsert() {
        // Mock do comportamento do repository
        when(repository.save(any(Product.class))).thenReturn(new Product(1L, "Teste", 10.0));

        // Chamada do método a ser testado
        ProductDTO result = service.insert(dto);

        // Verificação do resultado
        assertEquals(1L, result.getId());
        assertEquals("Teste", result.getName());
        assertEquals(10.0, result.getPrice());

        // Verificação do comportamento do repository
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdate() {
        // Mock do comportamento do repository
        when(repository.getReferenceById(any(Long.class))).thenReturn(new Product(1L, "Teste", 10.0));
        when(repository.save(any(Product.class))).thenReturn(new Product(1L, "Teste Atualizado", 20.0));

        // Chamada do método a ser testado
        ProductDTO result = service.update(1L, dto);

        // Verificação do resultado
        assertEquals(1L, result.getId());
        assertEquals("Teste Atualizado", result.getName());
        assertEquals(20.0, result.getPrice());

        // Verificação do comportamento do repository
        verify(repository, times(1)).getReferenceById(any(Long.class));
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    public void insertShouldReturnInvalidDataExceptionWhenNameIsBlank() {
        dto.setName("");
        assertThrows(InvalidDataException.class, () -> service.insert(dto));

    }

    @Test
    public void updateShouldReturnInvalidDataExceptionWhenNameIsBlank() {
        dto.setName("");
        assertThrows(InvalidDataException.class, () -> service.update(1L, dto));
    }

    @Test
    public void insertShouldReturnInvalidDataExceptionWhenPriceIsZero() {
        dto.setPrice(0.0);
        assertThrows(InvalidDataException.class, () -> service.insert(dto));
        InvalidDataException error = assertThrows(InvalidDataException.class, () -> service.insert(dto));
        assertEquals("Campo preco inválido", error.getMessage());
    }

    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.getReferenceById(any(Long.class))).thenThrow(EntityNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, dto));
    }

}
