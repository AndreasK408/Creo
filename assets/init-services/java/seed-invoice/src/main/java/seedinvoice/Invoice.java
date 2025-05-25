package seedinvoice;

import com.github.javafaker.Faker;
import org.bson.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class Address {
    String firstName;
    String lastName;
    String street;
    int number;
    int zipCode;
    String city;
    String country;

    private static final Faker faker = new Faker(new Locale("en-US"));

    Address() {
        this.firstName = faker.name().firstName();
        this.lastName = faker.name().lastName();
        this.street = faker.address().streetName();
        this.number = Integer.parseInt(faker.address().streetAddressNumber()); // Faker gibt String zurück
        this.zipCode = Integer.parseInt(faker.address().zipCode().replaceAll("[^0-9]", "").substring(0, Math.min(faker.address().zipCode().replaceAll("[^0-9]", "").length(), 5))); // Nur Ziffern, max 5
        if (this.zipCode == 0) this.zipCode = faker.number().numberBetween(10000,99999); // Fallback
        this.city = faker.address().city();
        this.country = faker.address().countryCode(); // Kurzer Ländercode
    }

    Document toDocument() {
        return new Document("first_name", firstName)
                .append("last_name", lastName)
                .append("street", street)
                .append("number", number)
                .append("zip_code", zipCode)
                .append("city", city)
                .append("country", country);
    }
}

class Item {
    long priceInCents; // u64 in Rust, kann groß sein
    String name;
    private static final Faker faker = new Faker(new Locale("en-US"));

    Item() {
        this.priceInCents = faker.number().numberBetween(100L, 10000000L); // 1 Euro bis 100.000 Euro
        this.name = faker.commerce().productName();
    }
    Document toDocument() {
        return new Document("price_in_cents", priceInCents)
                .append("name", name);
    }
}

class OrderItem {
    Item item;
    long quantity; // u64 in Rust
    private static final Faker faker = new Faker(new Locale("en-US"));


    OrderItem() {
        this.item = new Item();
        this.quantity = faker.number().numberBetween(1L, 100L);
    }
    Document toDocument() {
        return new Document("item", item.toDocument())
                .append("quantity", quantity);
    }
}

enum InvoiceStatus {
    OPEN,
    PAID
}

public class Invoice {
    private long id; // _id
    private List<OrderItem> items;
    private Address billingAddress;
    private Address shippingAddress;
    private String userId;
    private double taxRate; // f32 in Rust
    private Instant issuedAt;
    private String extraInfo;
    private InvoiceStatus status;
    private String invoiceNumber;

    private static final Faker faker = new Faker(new Locale("en-US"));
    private static final Random random = new Random();

    public Invoice(long id) {
        this.id = id;
        this.items = new ArrayList<>();
        int itemCount = random.nextInt(99) + 1; // 1 bis 100 Items
        for (int i = 0; i < itemCount; i++) {
            this.items.add(new OrderItem());
        }
        this.billingAddress = new Address();
        this.shippingAddress = new Address(); // Kann gleich oder anders sein
        this.userId = faker.internet().uuid(); // Generiert eine zufällige User-ID
        this.taxRate = 0.15; // Wie im Python/Rust default
        this.issuedAt = faker.date().past(365, TimeUnit.DAYS).toInstant(); // Zufälliges Datum im letzten Jahr
        this.extraInfo = faker.lorem().sentence();
        this.status = random.nextBoolean() ? InvoiceStatus.PAID : InvoiceStatus.OPEN; // Zufälliger Status
        this.invoiceNumber = "INV-" + faker.number().digits(10);
    }

    public org.bson.Document toDocument() {
        List<Document> itemDocs = this.items.stream()
                .map(OrderItem::toDocument)
                .collect(Collectors.toList());
        return new org.bson.Document("_id", this.id)
                .append("items", itemDocs)
                .append("billing_address", this.billingAddress.toDocument())
                .append("shipping_address", this.shippingAddress.toDocument())
                .append("user_id", this.userId)
                .append("tax_rate", this.taxRate)
                .append("issued_at", Date.from(this.issuedAt)) // Konvertiere zu java.util.Date für MongoDB
                .append("extra_info", this.extraInfo)
                .append("status", this.status.name()) // Speichere Enum als String
                .append("invoice_number", this.invoiceNumber);
    }
}
     