package invoice_update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude; // Wichtig für $set Logik

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
    final long priceInCents;
    final String name;

    @JsonCreator
    Item(@JsonProperty("price_in_cents") long priceInCents, @JsonProperty("name") String name) {
        this.priceInCents = priceInCents;
        this.name = name;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class OrderItem {
    final Item item;
    final long quantity;

    @JsonCreator
    OrderItem(@JsonProperty("item") Item item, @JsonProperty("quantity") long quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}

enum InvoiceStatus {
    OPEN,
    PAID
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceUpdatePayload {
    private final List<OrderItem> items;
    private final Address billingAddress;
    private final Address shippingAddress;
    private final Double taxRate;
    private final String extraInfo;
    private final InvoiceStatus status;

    @JsonCreator
    public InvoiceUpdatePayload(
            @JsonProperty("items") List<OrderItem> items,
            @JsonProperty("billing_address") Address billingAddress,
            @JsonProperty("shipping_address") Address shippingAddress,
            @JsonProperty("tax_rate") Double taxRate,
            @JsonProperty("extra_info") String extraInfo,
            @JsonProperty("status") InvoiceStatus status) {
        this.items = items;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.taxRate = taxRate;
        this.extraInfo = extraInfo;
        this.status = status;
    }

    public List<OrderItem> getItems() { return items; }
    public Address getBillingAddress() { return billingAddress; }
    public Address getShippingAddress() { return shippingAddress; }
    public Double getTaxRate() { return taxRate; }
    public String getExtraInfo() { return extraInfo; }
    public InvoiceStatus getStatus() { return status; }
}