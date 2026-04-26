# VoteBan Mod (Fabric 1.21.11)

A lightweight server-side moderation mod that allows players to vote-ban others in a fair and controlled way.

## Features

* 🗳️ Start vote bans with a reason
* 👍 / 👎 Player voting system
* ⏱️ 60-second vote duration
* 📊 Requires minimum votes + percentage to pass
* 🚫 Temporary bans instead of kicks
* 🛡️ Admin protection system
* ⚙️ Configurable ban time (1h / 1d / permanent)

---

## Commands

### Player Commands

```bash
/voteban start <player> <reason>
/voteban yes
/voteban no
/voteban status
```

---

### Admin Commands

```bash
/votebanadmin add <player>
/votebanadmin remove <player>
/votebanadmin bantime <1h | 1d | permanent>
```

---

## Installation

1. Install Fabric Loader (1.21.11)
2. Install Fabric API
3. Place the mod `.jar` into your server's `mods` folder
4. Start the server

⚠️ This is a **server-side mod** — clients do NOT need to install it.

---

## How It Works

* A vote lasts 60 seconds
* Minimum 3 votes required
* ~70% YES votes needed to pass
* If passed → target is temporarily banned

---

## Future Plans

* Config file support
* Vote cooldowns
* GUI voting system
* Better scaling based on player count

---

## License

MIT / your choice

---

## Contributing

Suggestions and pull requests are welcome!
