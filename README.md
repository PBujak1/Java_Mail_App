# Java Mail App

A desktop email client built in **Java Swing**, featuring account registration with admin authorization, sending and receiving real emails, and full message management (inbox, sent, trash) backed by a SQL database.

![Java](https://img.shields.io/badge/Java-100%25-orange)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue)
![SQL](https://img.shields.io/badge/Database-SQL-lightgrey)

---

## Features

- **Account registration** — create a new mailbox with a required `@fastdove.pl` domain, complete with validation for email format/uniqueness, required fields, and birth date format (`YYYY-MM-DD`).
- **Admin authorization** — new accounts must be manually approved by an administrator before they can log in. A notification is automatically sent to `admin@fastdove.pl` when a new account is created.
- **Login system** — authenticate with email and password, with error handling for invalid credentials.
- **Inbox, Sent & Trash** — browse received, sent, and deleted messages from a unified main window; click any message to open and read it.
- **Delete to trash** — removing a message from the inbox moves it to the trash instead of permanently deleting it.
- **Compose & send mail** — send messages to any recipient with a subject, message body, and optional **file attachment**. Sending has been verified to work correctly with external providers such as Onet and Gmail.
- **Account settings** — update your surname or birth date, or permanently delete your account (which removes it from the database and revokes login access).
- **Refresh** — fetch newly received mail from the connected mail server on demand, without needing to log out and back in.

## Architecture

The application follows a simple polymorphic design for refreshing mail state:

- **`Odswiezalny`** — an interface that enforces a single method:
  ```java
  void odswiez(String current, String password);
  ```
- **`OknoGlowne`** — the application's main window, which implements `Odswiezalny`:
  ```java
  public class OknoGlowne extends JFrame implements Odswiezalny
  ```
  When refresh is triggered, `OknoGlowne` connects to the mail server via `MailReceiver`, fetches and persists new messages to the database, then reloads the inbox list from the database.

Messages are loaded from the database into the UI through dedicated methods for each mailbox:
- `loadReceivedEmails()` — populates the inbox from the `received_emails` table.
- `loadSentEmails()` — populates the sent list from the `sent_emails` table.
- `loadBinEmails()` — populates the trash from the `bin` table.

Each query is scoped to the currently logged-in user (`currentUserEmail`), and results are mapped into `JList` models paired with parallel lists holding message bodies and titles.

## Getting Started

### Prerequisites
- Java (JDK) installed
- A SQL database instance (e.g. MySQL) with the required tables (`received_emails`, `sent_emails`, `bin`, and a users/accounts table)
- Valid mail account credentials for sending/receiving mail through the configured mail server

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/PBujak1/Java_Mail_App.git
   ```
2. Configure your database connection (see `DatabaseConnector`) with your SQL credentials.
3. Build and run the project from your preferred Java IDE, or via the command line.

## How It Works

1. **Launch** — the app opens on a login screen with an option to create an account.
   <img width="945" height="712" alt="image" src="https://github.com/user-attachments/assets/de0385db-1a0b-4d81-947b-95a558d85cb4" />

2. **Create an account** — fill in first name, last name, a new email ending in `@fastdove.pl`, birth date, and a password. Invalid emails, duplicate accounts, or malformed birth dates trigger a validation message.
   <img width="945" height="694" alt="image" src="https://github.com/user-attachments/assets/93c00ff1-cbb2-44b1-adfb-bfa78e3ddf15" />

3. **Await authorization** — after registration you're returned to the login screen; an admin must approve the account before you can sign in.
4. **Log in** — sign in with your approved email and password.
   <img width="945" height="719" alt="image" src="https://github.com/user-attachments/assets/911b8768-dc15-4975-ac74-a2614bb6f0c4" />

5. **Browse mail** — the main window displays received, sent, and trashed messages. Click a message to read it, and delete inbox messages to move them to the trash.
6. **Send mail** — use the compose panel in the top-left to write a new message, optionally attaching a file, and send it — delivery works with providers like Onet and Gmail.<img width="945" height="712" alt="image" src="https://github.com/user-attachments/assets/f3ee74fc-cc17-4d6d-bce8-f2157983fff9" />

7. **Manage your account** — update your surname or birth date, or delete your account entirely from the settings panel.
8. **Refresh** — use the refresh action to pull any newly arrived messages into the database and inbox without re-logging in.

> **Note:** Message rendering can occasionally be inconsistent with Onet-hosted mail; this issue has not been observed with Gmail.

## Tech Stack

- **Java** Swing for the GUI
- **JDBC** for database connectivity
- **SQL database** for storing accounts and messages
- Mail send/receive handled via a mail-fetching component (`MailReceiver`)
