import { Button, Checkbox, InputField, Theme, UIFactory, UIFactoryProvider } from "./UIThemeFactory";

// ── Step 6: Client — Dashboard Renderer ──────────────────────────────────────
// The renderer never imports any concrete class.
// It only knows UIFactory, Button, Checkbox, InputField.

class DashboardRenderer {
    private readonly button: Button;
    private readonly checkbox: Checkbox;
    private readonly input: InputField;

    constructor(factory: UIFactory) {
        // All components guaranteed to be from the same theme family
        this.button = factory.createButton("Export Report");
        this.checkbox = factory.createCheckbox("Enable Notifications");
        this.input = factory.createInputField("Search metrics...");
    }

    renderDashboard(): void {
        console.log("── Dashboard ─────────────────────────");
        console.log(this.button.render());
        console.log(this.checkbox.render());
        console.log(this.input.render());
        console.log("──────────────────────────────────────");
    }
}

// Usage — swap Theme.LIGHT to Theme.DARK and every component changes consistently
const userTheme = Theme.DARK;
const factory = UIFactoryProvider.getFactory(userTheme);
const dashboard = new DashboardRenderer(factory);
dashboard.renderDashboard();