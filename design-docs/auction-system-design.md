
# Auction Game System – Learning-Oriented Design Document

## 1. Purpose of This Project
This project is a **learning-first, resume-grade full‑stack system** built from scratch.
The goal is not speed, but **deep understanding** of:
- Backend‑driven business logic
- GraphQL API design
- Concurrency & time‑based rules
- Frontend state management
- Testing (backend + frontend)
- Clean architecture and reasoning

This document is the **single source of truth** for the system.

---

## 2. Tech Stack (Locked)

### Frontend
- React + TypeScript
- Tailwind CSS
- Apollo Client (GraphQL)
- React Router
- React Context + Hooks (Redux planned in V2)
- Testing: Jest + React Testing Library + MSW

### Backend
- Java + Spring Boot
- Spring GraphQL
- MySQL
- JWT-based authentication
- Testing:
  - Unit tests for services
  - Integration tests for GraphQL APIs

### Explicit Non‑Goals (V1)
- WebSockets (planned V2)
- Payments
- Mobile UI
- Chat
- Media/images
- CI/CD, Docker, Kubernetes

---

## 3. Core Roles

### Admin (Auctioneer)
- Start / pause / resume auction
- Select current player
- Enforce admin WAIT (indefinite pause)
- Force sell player (cannot override purse/squad limits)

### Team User
- Belongs to exactly one team
- 2 users per team
- Can place bids and use WAIT
- Single device assumption (V1)

---

## 4. Auction Rules

### Teams
- Total teams: 6
- Fixed purse: 100 cr
- Max squad size: 25

### Player Categories (Minimums enforced at end)
- Batters: 4
- Bowlers: 4
- All‑rounders: 3
- Wicket‑keepers: 2

### Players
- Preloaded into DB
- Base price varies per player
- Status:
  - AVAILABLE
  - SOLD
  - UNSOLD

---

## 5. Bidding Rules

### Bid Increments
- From base price up to 5 cr: +0.2 cr
- Above 5 cr: +0.5 cr
- Purse check enforced strictly

### Bid Behavior
- Bids cannot be withdrawn
- Bid history visible to all
- Backend is authoritative

### Concurrency
- Earlier server timestamp wins
- Later concurrent bid is still recorded as next valid bid

---

## 6. Time & WAIT Logic

### Timers
- Player appears → 2 minute timer
- On valid bid → timer resets to 30 seconds
- Backend rejects bids after timerEndAt

### WAIT
- Each team gets 2 WAITs per player
- Each WAIT adds +30 seconds cumulatively
- Allowed in accelerated auction
- Multiple teams can WAIT back‑to‑back

### Admin WAIT
- Pauses auction indefinitely
- Bids allowed
- Timer freezes and resumes on admin action

---

## 7. Auction Flow

1. Admin starts auction
2. Admin selects player
3. Backend sets currentPlayer + timer
4. Teams bid / WAIT
5. Admin marks SOLD or UNSOLD
6. UNSOLD players go to accelerated auction

---

## 8. Accelerated Auction
- Same rules as normal auction
- Same timer and WAIT behavior
- Admin retains full control

---

## 9. Backend Architecture (Learning‑First)

### Layers
- Controller (GraphQL Resolvers)
- Service (Business logic, transactions)
- Repository (DB access)
- Domain models (entities)

### Key Concepts to Learn
- Transactions & isolation
- Server‑side validation
- State machines
- Deterministic concurrency handling

---

## 10. GraphQL Design

### Queries (Examples)
- auctionState
- currentPlayer
- teams
- bidHistory

### Mutations (Examples)
- placeBid
- useWait
- pauseAuction
- resumeAuction
- markSold
- markUnsold

### Error Handling
- Backend returns structured error reasons
- Frontend displays messages
- Hybrid error handling approach

---

## 11. Frontend Architecture

### Philosophy
- Semi‑smart frontend
- Backend is the source of truth
- Frontend derives UI‑only state

### Data Fetching
- Hybrid polling (auction state polled every few seconds)
- Apollo cache for consistency

### State Management
- Auth & auction state via Context + hooks

---

## 12. Frontend UX Rules

- Disable bid button if clearly invalid
- Show remaining WAITs
- Show timer updates
- Show backend error messages
- Show purse (not auto‑calculated live)

---

## 13. Testing Strategy

### Backend
- Unit tests for services
- Integration tests for GraphQL APIs

### Frontend
- Component tests using RTL
- GraphQL mocked using MSW
- No E2E tests in V1

---

## 14. AI Usage Contract

### Allowed
- Concept explanations
- Code reviews
- Architecture critique
- Debugging help

### Forbidden by Default
- First‑draft business logic
- Timer logic
- Rule enforcement logic

Full code allowed **only when explicitly asked**.

---

## 15. Learning Checkpoints

At each milestone, stop and ask:
- Can I explain this without AI?
- Do I understand why this exists?
- Could I redesign this if requirements change?

---

## 16. V2 Evolution Path

- Polling → WebSockets / GraphQL Subscriptions
- Context → Redux Toolkit
- Admin undo flow
- Multi‑device support
- Deployment hardening

---

## End of Design Document
