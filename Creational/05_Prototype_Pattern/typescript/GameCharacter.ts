/**
 * An online RPG game needs to spawn thousands of enemy characters per session. 
 * Each enemy type (Warrior, Mage, Archer) has a complex base configuration — 
 * stats, abilities, equipment, AI behavior trees. Instead of rebuilding each 
 * enemy from scratch, the system clones pre-configured prototype templates 
 * and tweaks only the per-instance details (position, level scaling, name).
 */

// Scenario: Game Character Template System — Online RPG
// Clone pre-built enemy prototypes instead of constructing from scratch.
// Modify only instance-specific fields after cloning.

// ── Step 1: Prototype Interface ───────────────────────────────────────────────

interface Cloneable<T> {
    clone(): T;
}

// ── Step 2: Supporting Types ──────────────────────────────────────────────────

interface Stats {
    health:    number;
    mana:      number;
    attack:    number;
    defense:   number;
    speed:     number;
}

interface Equipment {
    weapon:  string;
    armor:   string;
    offhand: string;
}

interface Position {
    x: number;
    y: number;
    z: number;
}

type AIBehavior = "aggressive" | "defensive" | "patrol" | "ambush";

// ── Step 3: Concrete Prototype ────────────────────────────────────────────────
interface GameCharacterConfig {
    name:       string;
    type:       string;
    level:      number;
    stats:      Stats;
    equipment:  Equipment;
    abilities:  string[];
    position:   Position;
    aiBehavior: AIBehavior;
    isElite:    boolean;

}

class GameCharacter implements Cloneable<GameCharacter> {
    public id:         string;
    public name:       string;
    public type:       string;
    public level:      number;
    public stats:      Stats;
    public equipment:  Equipment;
    public abilities:  string[];
    public position:   Position;
    public aiBehavior: AIBehavior;
    public isElite:    boolean;

    constructor(config: GameCharacterConfig) {
        this.id         = `CHAR-${Math.random().toString(36).substr(2, 9).toUpperCase()}`;
        this.name       = config.name;
        this.type       = config.type;
        this.level      = config.level;
        this.stats      = config.stats;
        this.equipment  = config.equipment;
        this.abilities  = config.abilities;
        this.position   = config.position;
        this.aiBehavior = config.aiBehavior;
        this.isElite    = config.isElite;
    }

    /**
     * DEEP CLONE — every nested object is copied by value.
     * Modifying the clone's stats/equipment/abilities never touches the original.
     */
    clone(): GameCharacter {
        const cloned = new GameCharacter({
            name:       this.name,
            type:       this.type,
            level:      this.level,
            stats:      { ...this.stats },          // shallow copy of flat object = safe
            equipment:  { ...this.equipment },      // shallow copy of flat object = safe
            abilities:  [...this.abilities],        // new array — clone's abilities are independent
            position:   { ...this.position },      // new position object
            aiBehavior: this.aiBehavior,
            isElite:    this.isElite,
        });

        // Give the clone its own unique ID
        cloned.id = `CHAR-${Math.random().toString(36).substr(2, 9).toUpperCase()}`;
        return cloned;
    }

    summarize(): void {
        console.log(`── [${this.type}] ${this.name} (${this.id}) ──────────────`);
        console.log(`   Level:     ${this.level} | Elite: ${this.isElite}`);
        console.log(`   HP/MP:     ${this.stats.health}/${this.stats.mana}`);
        console.log(`   ATK/DEF:   ${this.stats.attack}/${this.stats.defense}`);
        console.log(`   Weapon:    ${this.equipment.weapon}`);
        console.log(`   Abilities: ${this.abilities.join(", ")}`);
        console.log(`   Position:  (${this.position.x}, ${this.position.y}, ${this.position.z})`);
        console.log(`   AI:        ${this.aiBehavior}`);
        console.log(`──────────────────────────────────────────────────`);
    }
}

// ── Step 4: Prototype Registry ────────────────────────────────────────────────

class CharacterPrototypeRegistry {
    private static readonly registry: Map<string, GameCharacter> = new Map<string, GameCharacter>();

    /** Register a master prototype under a key */
    static register(key: string, prototype: GameCharacter): void {
        CharacterPrototypeRegistry.registry.set(key, prototype);
        console.log(`[Registry] Prototype registered: "${key}"`);
    }

    /**
     * Get a fresh clone of a registered prototype.
     * Caller gets an independent copy — original master is never modified.
     */
    static getClone(key: string): GameCharacter {
        const prototype = CharacterPrototypeRegistry.registry.get(key);
        if (!prototype) {
            throw new Error(`[Registry] No prototype registered for key: "${key}"`);
        }
        return prototype.clone();
    }

    static listKeys(): void {
        console.log(`[Registry] Available prototypes: ${[...CharacterPrototypeRegistry.registry.keys()].join(", ")}`);
    }
}

export { GameCharacter, CharacterPrototypeRegistry };