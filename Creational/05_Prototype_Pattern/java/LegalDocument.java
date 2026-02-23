import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A Legal Tech platform needs to generate hundreds of legal documents daily — 
 * NDAs, employment contracts, service agreements. Each document type has a 
 * complex pre-configured template with clauses, formatting rules, metadata, 
 * and signatories. Instead of rebuilding templates on every generation request, 
 * the system clones the master template and fills in case-specific details.
 */

// Scenario: Document Template Engine — Legal Tech Platform
// Clone master legal document templates.
// Fill in case-specific details on the clone — master is never modified.

interface Prototype<T> {
    T clone();
}

// ── Step 2: Supporting Types ──────────────────────────────────────────────────

class Clause {
    private final String title;
    private       String content;

    public Clause(String title, String content) {
        this.title   = title;
        this.content = content;
    }

    // Copy constructor — used during deep cloning
    public Clause(Clause other) {
        this.title   = other.title;
        this.content = other.content;
    }

    public String getTitle()   { return title; }
    public String getContent() { return content; }
    public void   setContent(String content) { this.content = content; }

    @Override public String toString() {
        return String.format("  [%s]: %s", title, content.substring(0, Math.min(60, content.length())) + "...");
    }
}

class Signatory {
    private String name;
    private String role;
    private String email;

    public Signatory(String name, String role, String email) {
        this.name  = name;
        this.role  = role;
        this.email = email;
    }

    // Copy constructor
    public Signatory(Signatory other) {
        this.name  = other.name;
        this.role  = other.role;
        this.email = other.email;
    }

    public String getName()  { return name; }
    public String getRole()  { return role; }
    public String getEmail() { return email; }
    public void   setName(String name)   { this.name = name; }
    public void   setEmail(String email) { this.email = email; }

    @Override 
    public String toString() {
        return String.format("  %s (%s) — %s", name, role, email);
    }
}

// ── Step 3: Concrete Prototype ────────────────────────────────────────────────

public class LegalDocument implements Prototype<LegalDocument> {
    private String          documentId;
    private String          type;
    private String          title;
    private String          jurisdiction;
    private LocalDate       effectiveDate;
    private List<Clause>    clauses;
    private List<Signatory> signatories;
    private Map<String, String> metadata;
    private boolean         requiresNotarization;

    public LegalDocument(
        String type,
        String title,
        String jurisdiction,
        List<Clause> clauses,
        List<Signatory> signatories,
        Map<String, String> metadata,
        boolean requiresNotarization
    ) {
        this.documentId           = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type                 = type;
        this.title                = title;
        this.jurisdiction         = jurisdiction;
        this.effectiveDate        = LocalDate.now();
        this.clauses              = clauses;
        this.signatories          = signatories;
        this.metadata             = metadata;
        this.requiresNotarization = requiresNotarization;
    }

    /**
     * DEEP CLONE — every nested object (Clause, Signatory) is copied
     * using copy constructors. Modifying the clone never affects the master.
     */
    @Override
    public LegalDocument clone() {
        // Deep copy clauses — each Clause is a new object
        List<Clause> clonedClauses = new ArrayList<>();
        for (Clause clause : this.clauses) {
            clonedClauses.add(new Clause(clause));
        }

        // Deep copy signatories — each Signatory is a new object
        List<Signatory> clonedSignatories = new ArrayList<>();
        for (Signatory sig : this.signatories) {
            clonedSignatories.add(new Signatory(sig));
        }

        // Deep copy metadata map
        Map<String, String> clonedMetadata = new HashMap<>(this.metadata);

        LegalDocument cloned = new LegalDocument(
            this.type,
            this.title,
            this.jurisdiction,
            clonedClauses,
            clonedSignatories,
            clonedMetadata,
            this.requiresNotarization
        );

        // New unique ID for each cloned document
        cloned.documentId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return cloned;
    }

    // ── Fluent setters for post-clone customization ───────────────────────────

    public LegalDocument withTitle(String title) {
        this.title = title;
        return this;
    }

    public LegalDocument withEffectiveDate(LocalDate date) {
        this.effectiveDate = date;
        return this;
    }

    public LegalDocument withJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
        return this;
    }

    public LegalDocument addMetadata(String key, String value) {
        this.metadata.put(key, value);
        return this;
    }

    public List<Clause>    getClauses()     { return clauses; }
    public List<Signatory> getSignatories() { return signatories; }
    public String          getDocumentId()  { return documentId; }

    public void summarize() {
        System.out.println("── Legal Document ────────────────────────────────────");
        System.out.println("ID:           " + documentId);
        System.out.println("Type:         " + type);
        System.out.println("Title:        " + title);
        System.out.println("Jurisdiction: " + jurisdiction);
        System.out.println("Effective:    " + effectiveDate);
        System.out.println("Notarization: " + requiresNotarization);
        System.out.println("Clauses:");
        clauses.forEach(System.out::println);
        System.out.println("Signatories:");
        signatories.forEach(System.out::println);
        System.out.println("Metadata:     " + metadata);
        System.out.println("─────────────────────────────────────────────────────");
    }
}

// ── Step 4: Prototype Registry ────────────────────────────────────────────────

class DocumentTemplateRegistry {
    private static final Map<String, LegalDocument> registry = new HashMap<>();

    public static void register(String key, LegalDocument template) {
        registry.put(key, template);
        System.out.println("[Registry] Template registered: \"" + key + "\"");
    }

    public static LegalDocument getClone(String key) {
        LegalDocument prototype = registry.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("[Registry] No template for key: " + key);
        }
        return prototype.clone();
    }
}

// ── Step 5: Register Master Templates (done once at application startup) ───────

class DocumentBootstrapper {
    public static void registerTemplates() {

        // Master NDA template
        LegalDocument ndaTemplate = new LegalDocument(
            "NDA",
            "Non-Disclosure Agreement",
            "Maharashtra, India",
            new ArrayList<>(List.of(
                new Clause("Confidentiality",    "All information shared between parties shall be kept strictly confidential..."),
                new Clause("Term",               "This agreement shall remain in effect for a period of two (2) years..."),
                new Clause("Governing Law",      "This agreement shall be governed by the laws of Maharashtra, India..."),
                new Clause("Breach & Remedies",  "Any breach of this agreement entitles the non-breaching party to seek...")
            )),
            new ArrayList<>(List.of(
                new Signatory("PARTY_A_NAME", "Disclosing Party", "PARTY_A_EMAIL"),
                new Signatory("PARTY_B_NAME", "Receiving Party",  "PARTY_B_EMAIL")
            )),
            new HashMap<>(Map.of("template_version", "3.1", "approved_by", "Legal Dept")),
            false
        );

        // Master Employment Contract template
        LegalDocument employmentTemplate = new LegalDocument(
            "EMPLOYMENT",
            "Employment Agreement",
            "Maharashtra, India",
            new ArrayList<>(List.of(
                new Clause("Role & Responsibilities", "The Employee shall serve in the capacity of ROLE_PLACEHOLDER..."),
                new Clause("Compensation",            "The Employee shall receive a gross annual salary of SALARY_PLACEHOLDER..."),
                new Clause("Probation",               "The first 90 days of employment shall constitute a probationary period..."),
                new Clause("Termination",             "Either party may terminate this agreement with 30 days written notice...")
            )),
            new ArrayList<>(List.of(
                new Signatory("EMPLOYEE_NAME", "Employee",            "EMPLOYEE_EMAIL"),
                new Signatory("HR_MANAGER",    "HR Representative",  "hr@company.com"),
                new Signatory("DIRECTOR_NAME", "Authorized Director", "director@company.com")
            )),
            new HashMap<>(Map.of("template_version", "2.4", "approved_by", "HR & Legal")),
            true
        );

        DocumentTemplateRegistry.register("nda",        ndaTemplate);
        DocumentTemplateRegistry.register("employment", employmentTemplate);
    }
}