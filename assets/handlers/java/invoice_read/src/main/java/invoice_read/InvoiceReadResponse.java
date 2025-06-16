package invoice_read;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Address {
    @JsonProperty("first_name") String firstName;
    @JsonProperty("last_name") String lastName;
    @JsonProperty("street") String street;
    @JsonProperty("number") int number;
    @JsonProperty("zip_code") int zipCode;
    @JsonProperty("city") String city;
    @JsonProperty("country") String country;

    static Address fromDocument(Document doc) {
        if (doc == null) return null;
        Address addr = new Address();
        addr.firstName = doc.getString("first_name");
        addr.lastName = doc.getString("last_name");
        addr.street = doc.getString("street");
        addr.number = doc.getInteger("number", 0);
        addr.zipCode = doc.getInteger("zip_code", 0);
        addr.city = doc.getString("city");
        addr.country = doc.getString("country");
        return addr;
    }
}

class Item {
    @JsonProperty("price_in_cents") int priceInCents;
    @JsonProperty("name") String name;

    static Item fromDocument(Document doc) {
        if (doc == null) return null;
        Item item = new Item();
        Long priceLong = doc.getLong("price_in_cents");
        item.priceInCents = (priceLong != null) ? priceLong.intValue() : 0;
        item.name = doc.getString("name");
        return item;
    }
}

class OrderItem {
    @JsonProperty("item") Item item;
    @JsonProperty("quantity") int quantity;

    static OrderItem fromDocument(Document doc) {
        if (doc == null) return null;
        OrderItem orderItem = new OrderItem();
        orderItem.item = Item.fromDocument(doc.get("item", Document.class));
        Long quantityLong = doc.getLong("quantity");
        orderItem.quantity = (quantityLong != null) ? quantityLong.intValue() : 0;
        return orderItem;
    }
}

// Enum für den Status
enum InvoiceStatus {
    OPEN,
    PAID
}

public class InvoiceReadResponse {
    private final String id;
    private final List<OrderItem> items;
    private final Address billingAddress;
    private final Address shippingAddress;
    private final String userId;
    private final double taxRate; // double für float
    private final long issuedAt; // Unix-Timestamp (long)
    private final String extraInfo;
    private final InvoiceStatus status;
    private final String invoiceNumber;

    private InvoiceReadResponse(String id, List<OrderItem> items, Address billingAddress,
                                Address shippingAddress, String userId, double taxRate,
                                long issuedAt, String extraInfo, InvoiceStatus status,
                                String invoiceNumber) {
        this.id = id;
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

    public static InvoiceReadResponse fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        Long idLong = doc.getLong("_id");
        String idString = (idLong != null) ? idLong.toString() : null;
        List<Document> itemDocs = doc.getList("items", Document.class, new ArrayList<>());
        List<OrderItem> orderItems = itemDocs.stream()
                .map(OrderItem::fromDocument)
                .collect(Collectors.toList());
        Address billingAddr = Address.fromDocument(doc.get("billing_address", Document.class));
        Address shippingAddr = Address.fromDocument(doc.get("shipping_address", Document.class));
        String userId = doc.getString("user_id");
        double taxRate = doc.getDouble("tax_rate");
        String extraInfo = doc.getString("extra_info");
        String invoiceNumber = doc.getString("invoice_number");
        InvoiceStatus status = InvoiceStatus.OPEN;
        String statusStr = doc.getString("status");
        if (statusStr != null) {
            try {
                status = InvoiceStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warnung: Ungültiger Statuswert '" + statusStr + "' in DB gefunden, verwende Default OPEN.");
            }
        }

        long issuedAtTimestamp = 0;
        Object issuedAtObj = doc.get("issued_at");
        if (issuedAtObj instanceof Date) {
            issuedAtTimestamp = ((Date) issuedAtObj).getTime() / 1000L;
        } else if (issuedAtObj instanceof Instant) {
            issuedAtTimestamp = ((Instant) issuedAtObj).getEpochSecond();
        } else if (issuedAtObj instanceof Long) {
            issuedAtTimestamp = (Long) issuedAtObj;
        }

        return new InvoiceReadResponse(idString, orderItems, billingAddr, shippingAddr, userId,
                taxRate, issuedAtTimestamp, extraInfo, status, invoiceNumber);
    }

    @JsonProperty("id") public String getId() { return id; }
    @JsonProperty("items") public List<OrderItem> getItems() { return items; }
    @JsonProperty("billing_address") public Address getBillingAddress() { return billingAddress; }
    @JsonProperty("shipping_address") public Address getShippingAddress() { return shippingAddress; }
    @JsonProperty("user_id") public String getUserId() { return userId; }
    @JsonProperty("tax_rate") public double getTaxRate() { return taxRate; }
    @JsonProperty("issued_at") public long getIssuedAt() { return issuedAt; }
    @JsonProperty("extra_info") public String getExtraInfo() { return extraInfo; }
    @JsonProperty("status") public InvoiceStatus getStatus() { return status; }
    @JsonProperty("invoice_number") public String getInvoiceNumber() { return invoiceNumber; }
}