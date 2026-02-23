import java.time.LocalDate;

// ── Step 6: Client — Generate documents by cloning, not constructing ──────────

public class DocumentGenerationService {
    public static void main(String[] args) {
        DocumentBootstrapper.registerTemplates();
        System.out.println();
        
        // Generate NDA for a specific deal — clone and customize
        LegalDocument ndaForAcmeDeal = DocumentTemplateRegistry.getClone("nda");
        ndaForAcmeDeal
            .withTitle("NDA — Acme Corp & TechStartup Pvt Ltd")
            .withEffectiveDate(LocalDate.of(2026, 3, 1))
            .addMetadata("deal_ref", "ACME-2026-001");

        // Customize signatories on the clone — master template untouched
        ndaForAcmeDeal.getSignatories().get(0).setName("Rajesh Kumar");
        ndaForAcmeDeal.getSignatories().get(0).setEmail("rajesh@acmecorp.com");
        ndaForAcmeDeal.getSignatories().get(1).setName("Priya Sharma");
        ndaForAcmeDeal.getSignatories().get(1).setEmail("priya@techstartup.io");

        // Generate Employment Contract for a new hire
        LegalDocument employmentForAnanya = DocumentTemplateRegistry.getClone("employment");
        employmentForAnanya
            .withTitle("Employment Agreement — Ananya Iyer")
            .withEffectiveDate(LocalDate.of(2026, 4, 1));

        // Customize clauses on the clone
        employmentForAnanya.getClauses().get(0)
            .setContent("The Employee shall serve in the capacity of Senior Software Engineer...");
        employmentForAnanya.getClauses().get(1)
            .setContent("The Employee shall receive a gross annual salary of ₹24,00,000...");
        employmentForAnanya.getSignatories().get(0).setName("Ananya Iyer");
        employmentForAnanya.getSignatories().get(0).setEmail("ananya.iyer@email.com");

        ndaForAcmeDeal.summarize();
        employmentForAnanya.summarize();
    }
}