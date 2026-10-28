package com.example.contacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.contacts.data.ContactRepository;
import com.example.contacts.data.InMemoryContactRepository;
import com.example.contacts.domain.model.Contact;
import com.example.contacts.domain.validator.ContactValidator;
import com.example.contacts.domain.validator.ValidationResult;
import com.example.contacts.util.ExtensionsKt;

import org.junit.Test;

/** Chứng minh interop: Java gọi trực tiếp code Kotlin (constructor @JvmOverloads, @JvmField, extension). */
public class KotlinInteropJavaTest {
    @Test
    public void javaCallsKotlinRepositoryAndValidator() {
        ContactRepository repo = new InMemoryContactRepository(); // tham số mặc định: danh bạ rỗng
        assertEquals(0, repo.getAll().size());
        repo.add(new Contact("An", "0901234567"));

        ContactValidator validator = new ContactValidator(repo);
        ValidationResult duplicate = validator.validate("A", "0901234567"); // overload bỏ editingId
        assertNotNull(duplicate.phoneError); // @JvmField
        assertNull(validator.validate("A", "0999999999").phoneError);

        repo.add(new Contact("Dũng", "0999999999")); // overload bỏ các tham số mặc định
        assertEquals(2, repo.stats().getTotal());
    }

    @Test
    public void javaUsesKotlinExtension() {
        assertEquals("N", ExtensionsKt.avatarLetter("  nam"));
    }
}
