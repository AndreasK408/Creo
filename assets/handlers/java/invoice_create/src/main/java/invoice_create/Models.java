package invoice_create;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Um unbekannte Felder zu ignorieren
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class Address {
    final String firstName;
    final String lastName;
    final String street;
    final int number;
    final int zipCode;
    final String city;
    final String country;

    @JsonCreator
    Address(
            @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName,
            @JsonProperty("street") String street, @JsonProperty("number") int number,
            @JsonProperty("zip_code") int zipCode, @JsonProperty("city") String city,
            @JsonProperty("country") String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Item {
    final int priceInCents; // Java int
    final String name;

    @JsonCreator
    Item(@JsonProperty("price_in_cents") int priceInCents, @JsonProperty("name") String name) {
        this.priceInCents = priceInCents;
        this.name = name;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class OrderItem {
    final Item item;
    final int quantity; // Java int

    @JsonCreator
    OrderItem(@JsonProperty("item") Item item, @JsonProperty("quantity") int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}

// Enum für den Status, entspricht Literal/Enum in Python/Rust
enum InvoiceStatus {
    OPEN,
    PAID
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Invoice {
    final List<OrderItem> items;
    final Address billingAddress;
    final Address shippingAddress;
    final String userId;
    Double taxRate;
    Instant issuedAt;
    String extraInfo;
    InvoiceStatus status;
    final String invoiceNumber;

    @JsonCreator
    Invoice(
            @JsonProperty("items") List<OrderItem> items,
            @JsonProperty("billing_address") Address billingAddress,
            @JsonProperty("shipping_address") Address shippingAddress,
            @JsonProperty("user_id") String userId,
            @JsonProperty("tax_rate") Double taxRate, // Kann null sein
            @JsonProperty("issued_at") Instant issuedAt, // Kann null sein
            @JsonProperty("extra_info") String extraInfo, // Kann null sein
            @JsonProperty("status") InvoiceStatus status, // Kann null sein
            @JsonProperty("invoice_number") String invoiceNumber) {
        this.items = items;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.userId = userId;
        this.taxRate = taxRate;
        this.issuedAt = issuedAt;
        this.extraInfo = extraInfo;
        this.status = status;
        this.invoiceNumber = invoiceNumber;
    }

    void setTaxRate(Double taxRate) { this.taxRate = taxRate; }
    void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
    void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }
    void setStatus(InvoiceStatus status) { this.status = status; }

    public List<OrderItem> getItems() { return items; }
    public Address getBillingAddress() { return billingAddress; }
    public Address getShippingAddress() { return shippingAddress; }
    public String getUserId() { return userId; }
    public Double getTaxRate() { return taxRate; }
    public Instant getIssuedAt() { return issuedAt; }
    public String getExtraInfo() { return extraInfo; }
    public InvoiceStatus getStatus() { return status; }
    public String getInvoiceNumber() { return invoiceNumber; }

}