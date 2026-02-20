/**
 * A SaaS analytics dashboard supports Light and Dark themes.
 * Every UI component rendered — buttons, checkboxes, input fields —
 * must consistently belong to the same theme. The rendering engine
 * never knows which theme is active; it just calls the factory.
 */

// Scenario: UI Theme Engine — SaaS Analytics Dashboard
// The renderer asks the factory for components.
// Factory guarantees every component belongs to the same theme family.

// ── Step 1: Abstract Product Interfaces ───────────────────────────────────────
interface Button {
    render(): string;
    onClick(handler: () => void): void;
}

interface Checkbox {
    render(): string;
    toggle(): boolean;
}

interface InputField {
    render(): string;
    getValue(): string;
}

// ── Step 2: Abstract Factory Interface ────────────────────────────────────────
interface UIFactory {
    createButton(label: string): Button;
    createCheckbox(label: string): Checkbox;
    createInputField(placeholder: string): InputField;
}

// ── Step 3A: Light Theme Concrete Products ────────────────────────────────────
class LightButton implements Button {
    private handler?: () => void;
    constructor(private readonly label: string) {}

    render(): string {
        return `<button style="background:#FFFFFF; color:#000000; border:1px solid #CCCCCC">
      ${this.label}
    </button>`;
    }

    onClick(handler: () => void): void {
        this.handler = handler;
        console.log(`[LightButton] onClick registered for "${this.label}"`);
    }
}

class LightCheckbox implements Checkbox {
    private checked = false;
    constructor(private readonly label: string) {}

    render(): string {
        return `<input type="checkbox" style="accent-color:#0078D4"> ${this.label}`;
    }

    toggle(): boolean {
        this.checked = !this.checked;
        console.log(
            `[LightCheckbox] "${this.label}" is now ${this.checked ? "✅ checked" : "⬜ unchecked"}`,
        );
        return this.checked;
    }
}

class LightInputField implements InputField {
    private value = "";
    constructor(private readonly placeholder: string) {}

    render(): string {
        return `<input style="background:#FFFFFF; border:1px solid #CCCCCC; color:#000000"
      placeholder="${this.placeholder}" />`;
    }

    getValue(): string {
        return this.value;
    }
}

// ── Step 3B: Dark Theme Concrete Products ─────────────────────────────────────
class DarkButton implements Button {
    private handler?: () => void;
    constructor(private readonly label: string) {}

    render(): string {
        return `<button style="background:#1E1E1E; color:#FFFFFF; border:1px solid #555555">
      ${this.label}
    </button>`;
    }

    onClick(handler: () => void): void {
        this.handler = handler;
        console.log(`[DarkButton] onClick registered for "${this.label}"`);
    }
}

class DarkCheckbox implements Checkbox {
    private checked = false;
    constructor(private readonly label: string) {}

    render(): string {
        return `<input type="checkbox" style="accent-color:#BB86FC"> ${this.label}`;
    }

    toggle(): boolean {
        this.checked = !this.checked;
        console.log(
            `[DarkCheckbox] "${this.label}" is now ${this.checked ? "✅ checked" : "⬜ unchecked"}`,
        );
        return this.checked;
    }
}

class DarkInputField implements InputField {
    private value = "";
    constructor(private readonly placeholder: string) {}

    render(): string {
        return `<input style="background:#2D2D2D; border:1px solid #555555; color:#FFFFFF"
      placeholder="${this.placeholder}" />`;
    }

    getValue(): string {
        return this.value;
    }
}

// ── Step 4: Concrete Factories ────────────────────────────────────────────────
class LightThemeFactory implements UIFactory {
    createButton(label: string): Button {
        return new LightButton(label);
    }

    createCheckbox(label: string): Checkbox {
        return new LightCheckbox(label);
    }

    createInputField(placeholder: string): InputField {
        return new LightInputField(placeholder);
    }
}

class DarkThemeFactory implements UIFactory {
    createButton(label: string): Button {
        return new DarkButton(label);
    }

    createCheckbox(label: string): Checkbox {
        return new DarkCheckbox(label);
    }

    createInputField(placeholder: string): InputField {
        return new DarkInputField(placeholder);
    }
}

// ── Step 5: Factory Selector ──────────────────────────────────────────────────

enum Theme {
    LIGHT = "LIGHT",
    DARK = "DARK",
}

class UIFactoryProvider {
    static getFactory(theme: Theme): UIFactory {
        switch (theme) {
            case Theme.LIGHT:
                return new LightThemeFactory();
            case Theme.DARK:
                return new DarkThemeFactory();
            default:
                throw new Error(`Unknown theme: ${theme}`);
        }
    }
}
export { UIFactoryProvider, Theme, UIFactory, Button, Checkbox, InputField };