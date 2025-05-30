# Branching Strategy - Trunk-Based Development

## 1. Introduction
This development strategy relies on having a single main branch, typically called `main`. Features are developed on short-lived branches and merged quickly into the main branch to ensure smooth and continuous workflow.

---

## 2. Branches Used
- **main**  
  The main stable branch that holds production-ready code.

- **feature/feature-name**  
  Short-lived branches created for small features or quick fixes. These branches are merged back into `main` quickly.

---

## 3. Workflow Rules
- Feature branches should be short-lived (typically a few hours to a maximum of two days).
- Features must be tested locally before merging.
- Branches are merged into `main` quickly, either via Pull Requests with light reviews or directly if working solo.
- Regularly update the `main` branch and pull the latest changes to avoid merge conflicts.
- Use **Feature Toggles** to merge incomplete features without affecting production.

---

## 4. Typical Workflow Steps
1. Switch to the `main` branch and update it:
   ```bash
   git checkout main
   git pull origin main
