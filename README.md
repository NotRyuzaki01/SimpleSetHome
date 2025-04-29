# SimpleSetHome

A lightweight Minecraft plugin that allows players to set a personal home location and teleport to it easily.  
Designed to be simple, fast, and beginner-friendly.

---

## 📦 Features
- `/sethome` — Save your current location as your home.
- `/home` — Open a GUI (bed button) to teleport to your saved home.
- Persistent storage: homes are saved and loaded automatically even after server restarts.
- Clean and simple codebase.
- No database required — uses simple YAML config.
- GUI-based teleportation with a Bed icon.

---

## 🛠 Commands

| Command | Description |
|:--------|:------------|
| `/sethome` | Saves your current location as your home. |
| `/home` | Opens a GUI to teleport to your home. |

---

## 🔧 Installation
1. Download the SimpleSetHome jar file.
2. Drop it into your server's `plugins/` folder.
3. Restart or reload your server.
4. You're ready to go!

---

## 📂 Configuration

| File | Purpose |
|:-----|:--------|
| `config.yml` | Stores all saved player homes. |
| `IgnoreGUI.yml` | (Optional) If you extend the plugin, use this to list GUI titles that shouldn't be affected by other plugins (e.g., lore injectors). |

You usually don't need to manually edit anything — homes are saved automatically.

---

## 🚀 Usage
1. Stand where you want your home to be.
2. Run `/sethome`.
3. Anytime later, run `/home`, click the Bed button, and teleport back!

---

## 🛡️ Requirements
- Minecraft 1.20+ (or 1.19+ should also work)
- Java 17 or higher

---

## 📜 License
This plugin under the MIT licence.

---
