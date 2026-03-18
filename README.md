<div align="center">

<img src="src/main/resources/icons/app_icon.png" width="80" alt="WellLogAnalyzer Icon"/>

# WellLogAnalyzer

**A modern, offline-capable hydraulics simulation tool for petroleum engineers.**

Built with Jetpack Compose Desktop · Kotlin · Clean Architecture

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Desktop](https://img.shields.io/badge/Compose%20Desktop-1.7.3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-F4A917?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-2EC4B6?style=flat-square)]()
[![Status](https://img.shields.io/badge/Status-Complete-2EC4B6?style=flat-square)]()

</div>

---

## Overview

WellLogAnalyzer is a professional desktop application that gives petroleum engineers a fast, modern alternative to expensive enterprise suites. It runs **fully offline** on any machine, requires no server, and produces results in seconds.

The tool covers the full hydraulics workflow — from entering drilling parameters and running ECD simulations, to visualizing results in interactive 2D charts and a 3D wellbore viewer, to exporting polished Excel and Word engineering reports.

---

## Features

| Feature | Status | Description |
|---|---|---|
| **Well Input** | ✅ Complete | Multi-tab parameter entry — drill string, bit, fluid properties, formation zones, deviation survey. Full validation + Excel import. |
| **Hydraulics Engine** | ✅ Complete | Depth-step solver emitting a live `Flow<PressurePoint>`. Bingham Plastic & Power Law rheology. ECD, APL, bit hydraulics, surge/swab. |
| **Simulation Screen** | ✅ Complete | Three-panel layout — run controls, live ECD mini-chart with progress, scrollable monospace log. Results summary with metric cards. |
| **2D Charts** | ✅ Complete | Pressure-Depth, ECD Profile, Annular Velocity, Bit Hydraulics bar chart, Component Breakdown — all with safe-window shading and legend. |
| **3D Viewer** | ✅ Complete | Interactive wellbore + geology viewer via Jzy3D / Swing interop. ECD colour-mapped tube, formation layer cylinders, camera controls. |
| **Report Export** | ✅ Complete | One-click Excel (.xlsx via Apache POI) and Word (.docx via Docx4j) engineering reports with cover page, KPI strip, and data tables. |
| **Dashboard** | ✅ Complete | Project overview with stat cards, recent wells table, quick actions, activity log, and simulations bar chart. ProtoBuf persistence. |
| **Settings** | ✅ Complete | 4 built-in themes (Petroleum Dark, Ocean Deep, Slate Light, Amber Day), unit system selector, decimal precision, file path pickers. |
| **Theme System** | ✅ Complete | `WellLogColors` CompositionLocal — theme changes recompose the entire app instantly. Selected theme persisted across restarts. |
| **Data Persistence** | ✅ Complete | All well profiles, simulation results, and app settings saved locally via `kotlinx-serialization-protobuf`. Atomic writes. |

---

## Themes

The app ships with 4 built-in themes switchable from the **palette icon in the top bar** or from **Settings**. The selection is persisted to disk and restored on every launch.

| Theme | Style | Accent |
|---|---|---|
| Petroleum Dark | Dark navy | Amber gold `#F4A917` |
| Ocean Deep | Dark deep blue | Cyan `#00D4FF` |
| Slate Light | Light gray | Warm amber `#B57A10` |
| Amber Day | Light warm | Amber `#F4A917` on brown sidebar |

---

## Tech Stack

| Concern | Library | Version |
|---|---|---|
| Language | Kotlin JVM | 2.1.0 |
| UI Framework | Jetpack Compose Desktop | 1.7.3 |
| Design System | Material Design 3 | — |
| Dependency Injection | Koin | 3.5.6 |
| Async | Kotlin Coroutines + Swing dispatcher | 1.8.1 |
| Serialization | kotlinx-serialization-protobuf | 1.7.3 |
| 2D Charts | Lets-Plot (AWT / Batik) | 4.5.2 |
| 3D Visualization | Jzy3D + FXYZ3D | 2.2.1 / 0.6.0 |
| Excel Export | Apache POI | 5.2.3 |
| Word Export | Docx4j (JAXB-ReferenceImpl) | 11.5.3 |
| Logging | SLF4J + Logback | 2.0.9 / 1.4.14 |
| JavaFX | OpenJFX | 21 |

---

## Architecture

The project follows **Clean Architecture** with a strict three-layer vertical slice per feature:

```
feature/
  domain/        ← Pure Kotlin. Zero dependencies on frameworks or libraries.
  data/          ← Implements domain interfaces. Handles files, POI, Jzy3D, ProtoBuf.
  presentation/  ← Compose UI + ViewModel. Observes StateFlow, emits callbacks only.
```

Dependency rule enforced strictly:

```
presentation → domain ← data
```

`data` and `presentation` never depend on each other.

### Project Structure

```
src/main/kotlin/com/oussama_chatri/
│
├── Main.kt / App.kt / AppState.kt
│
├── core/
│   ├── base/          BaseViewModel, UiState<T>
│   ├── di/            AppModule (Koin root)
│   ├── navigation/    Route, AppNavigation
│   ├── theme/         Color, Typography, Shape, AppTheme, WellLogColors (4 themes)
│   └── ui/components/ AppScaffold, SideNavBar, TopBar, LabeledTextField,
│                      SectionCard, StatusBadge, ErrorBanner, ConfirmDialog …
│
└── feature/
    ├── wellinput/     domain · data · presentation · di
    ├── simulation/    domain · data · presentation · di
    ├── charts2d/      domain · data · presentation · di
    ├── viewer3d/      domain · data · presentation · di
    ├── reports/       domain · data · presentation · di
    ├── dashboard/     domain · data · presentation · di
    └── settings/      domain · data · presentation · di
```

### Theme System

```
SettingsProtoStore (disk)
        ↓  read on startup
AppState.currentThemeId  ←  SettingsViewModel.setTheme()  ←  TopBar palette button
        ↓                                                       Settings screen
App.kt: AppTheme(themeId = appState.currentThemeId)
        ↓  provides LocalWellLogColors + MaterialTheme.colorScheme
Every composable using WellLogTheme.colors recomposes instantly
        ↓
SettingsProtoStore.write()  →  disk  →  restored on next launch
```

### Hydraulics Engine

All equations follow **Bourgoyne et al. — Applied Drilling Engineering (SPE Vol. 2)** and **API RP 13D** in field units:

| Calculation | Formula |
|---|---|
| Hydrostatic pressure | `P = 0.052 × MW × TVD` |
| Annular velocity | `va = q / (2.448 × (d₁² − d₂²))` ft/min |
| Bingham laminar APL | `dP/dL = PV·va / (60000·dh²) + YP / (200·dh)` |
| ECD | `ECD = MW + APL / (0.052 × TVD)` |
| Bit pressure drop | `ΔP = ρ·q² / (12031 × Cd² × TFA²)` |
| HSI | `HSI = HHP / A_bit` |
| Impact force | `F = 0.000518 × ρ × q × Vn` |

---

## Getting Started

### Prerequisites

- JDK 17 or higher
- IntelliJ IDEA 2023.3+ or any Gradle-compatible IDE

### Run

```bash
git clone https://github.com/your-username/WellLogAnalyzer.git
cd WellLogAnalyzer
./gradlew run
```

### Build Native Installer

```bash
# Linux 
./gradlew ./gradlew packageAppImage

```

Output: `build/compose/binaries/`

### Import a Well Profile from Excel

```bash
pip install openpyxl
python scripts/generate_well_profile.py
# → generates Well_07A_Profile.xlsx
```

Use **Import from Excel** in the Well Input screen to load it.

---

## Data Flow

```
User fills Well Input  →  ValidateWellProfileUseCase
                       ↓
              SaveWellProfileUseCase  →  WellProfileProtoStore (disk)
                       ↓
              RunSimulationUseCase  →  HydraulicsEngineImpl
                       ↓  emits Flow<PressurePoint>  (live UI progress)
              SimulationViewModel  →  StateFlow<SimulationStatus>
                       ↓
              SimulationResult  →  SimulationProtoStore (disk)
                       ↓
              Charts2DViewModel  ←  BuildChartDataUseCase
              Viewer3DViewModel  ←  ComputeWellTrajectoryUseCase
              ReportViewModel    ←  ExportExcelReportUseCase / ExportWordReportUseCase
              DashboardViewModel ←  GetRecentProjectsUseCase  ←  ProjectProtoStore
```

---

## License

Apache License 2.0 — see [LICENSE](LICENSE) for details.

---

<div align="center">
Built with 🛢️ by <a href="https://github.com/your-username">Oussama Chatri</a>
<br/>
<sub>Jetpack Compose Desktop · Kotlin · Clean Architecture · API RP 13D</sub>
</div>