import { CharacterPrototypeRegistry, GameCharacter } from "./GameCharacter";

// ── Step 5: Register Master Prototypes (done once at game startup) ────────────

const warriorPrototype = new GameCharacter({
    name: "Warrior",
    type: "WARRIOR",
    level: 1,
    stats: { health: 500, mana: 100, attack: 80, defense: 120, speed: 40 },
    equipment: {
        weapon: "Iron Sword",
        armor: "Chain Mail",
        offhand: "Wooden Shield",
    },
    abilities: ["Slash", "Block", "War Cry"],
    position: { x: 0, y: 0, z: 0 },
    aiBehavior: "aggressive",
    isElite: false,
});

const magePrototype = new GameCharacter({
    name: "Mage",
    type: "MAGE",
    level: 1,
    stats: { health: 250, mana: 500, attack: 150, defense: 40, speed: 60 },
    equipment: {
        weapon: "Arcane Staff",
        armor: "Cloth Robe",
        offhand: "Spell Tome",
    },
    abilities: ["Fireball", "Frost Nova", "Arcane Shield", "Teleport"],
    position: { x: 0, y: 0, z: 0 },
    aiBehavior: "defensive",
    isElite: false,
});

CharacterPrototypeRegistry.register("warrior", warriorPrototype);
CharacterPrototypeRegistry.register("mage", magePrototype);

// ── Step 6: Client — Spawn enemies by cloning, not constructing ───────────────

// Spawn 3 warrior instances — each is an independent clone
const warrior1 = CharacterPrototypeRegistry.getClone("warrior");
warrior1.name = "Cave Warrior Alpha";
warrior1.position = { x: 120, y: 0, z: 340 };
warrior1.level = 5;
warrior1.stats.health = 750; // Scale up for level 5 — original untouched

const warrior2 = CharacterPrototypeRegistry.getClone("warrior");
warrior2.name = "Cave Warrior Beta";
warrior2.position = { x: 130, y: 0, z: 350 };
warrior2.isElite = true;
warrior2.equipment.weapon = "Steel Sword"; // Upgrade — original has "Iron Sword" still

const eliteMage = CharacterPrototypeRegistry.getClone("mage");
eliteMage.name = "Shadow Archmage";
eliteMage.level = 20;
eliteMage.isElite = true;
eliteMage.abilities.push("Meteor Storm"); // Add ability to clone — original unaffected
eliteMage.position = { x: 500, y: 100, z: 200 };

warrior1.summarize();
warrior2.summarize();
eliteMage.summarize();

// Proof: original prototype is unchanged
console.log("\n── Originals (prototypes) are untouched ──");
warriorPrototype.summarize();
magePrototype.summarize();
