<div align="center">

<img src="src/main/resources/icons/app_icon.png" width="80" alt="WellLogAnalyzer Icon"/>

# WellLogAnalyzer

**A modern, offline-capable hydraulics simulation tool for petroleum engineers.**

Built with Jetpack Compose Desktop · Kotlin · Clean Architecture

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Desktop](https://img.shields.io/badge/Compose%20Desktop-1.7.3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-F4A917?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-2EC4B6?style=flat-square)]()
[![Status](https://img.shields.io/badge/Status-In%20Development-E63946?style=flat-square)]()

</div>

---

## Overview

WellLogAnalyzer is a professional desktop application that gives petroleum engineers a fast, modern alternative to expensive enterprise suites. It runs **fully offline** on any machine, requires no server, and produces results in seconds.

The tool covers the full hydraulics workflow — from entering drilling parameters and running ECD simulations, to visualizing results in interactive 2D charts and a 3D wellbore viewer, to exporting polished Excel and Word engineering reports.

<div align="center">
<img src="docs/screenshots/simulation_screen.png" width="860" alt="Simulation Screen"/>
<br/>
<sub>Hydraulics simulation running on Well-07A — ECD profile, bit hydraulics, and live log panel</sub>
</div>

---

## Features

### ✅ Implemented

| Feature | Description |
|---|---|
| **Well Input** | Multi-tab parameter entry — drill string, bit configuration, fluid properties, formation zones, deviation survey. Full validation before simulation. Excel import via Apache POI. |
| **Hydraulics Engine** | Depth-step solver emitting a live `Flow<PressurePoint>`. Supports Bingham Plastic and Power Law rheology. Computes ECD, annular pressure loss, bit pressure drop, HSI, HHP, nozzle velocity, impact force, surge/swab. |
| **Simulation Screen** | Three-panel layout — run controls, live progress with mini ECD chart, and a monospace scrolling log panel. Results summary with metric cards after completion. |
| **Data Persistence** | All well profiles and simulation results persisted locally via ProtoBuf (kotlinx-serialization-protobuf). |

### 🔄 In Progress

| Feature | Description |
|---|---|
| **2D Charts** | Pressure vs. depth, ECD profile, annular velocity, bit hydraulics bar chart — rendered with Lets-Plot Compose. |
| **Report Export** | One-click Excel (.xlsx via Apache POI) and Word (.docx via Docx4j) engineering reports. |
| **3D Viewer** | Interactive wellbore + geology viewer using Jzy3D via Swing/AWT interop with Compose Desktop. |
| **Dashboard** | Project overview, recent wells, quick stats, simulation history. |
| **Settings** | Theme toggle, unit system (Metric/Imperial/API), default export paths, decimal precision. |

---

## Screenshots

| Well Input | Simulation |
|---|---|
| ![Well Input](docs/screenshots/well_input.png) | ![Simulation](docs/screenshots/simulation_screen.png) |

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
| Word Export | Docx4j | 11.5.3 |
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

The dependency rule is enforced strictly:

```
presentation → domain ← data
```

`data` and `presentation` never depend on each other. This means the hydraulics engine can be fully unit-tested without any UI, and swapping the 3D library only touches the `data` layer of the viewer feature.

### Hydraulics Engine

The simulation engine lives entirely in `domain/engine/` with zero library dependencies:

```
domain/engine/
  equations/
    AnnularVelocityCalculator.kt   ← ft/min velocity, Reynolds number, flow regime
    PressureDropCalculator.kt      ← Bourgoyne API RP 13D, Bingham + Power Law
    ECDCalculator.kt               ← ECD with formation zone interpolation
    BitHydraulicsCalculator.kt     ← HSI, HHP, nozzle velocity, impact force
    SurgeSwabCalculator.kt         ← tripping pressure estimation
  solver/
    DepthStepSolver.kt             ← emits Flow<PressurePoint>, cancellable
```

The solver emits one `PressurePoint` per depth step as a cold Kotlin `Flow`, allowing the UI to show a live progress bar and mini ECD chart while the calculation runs in the background.

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
│   ├── theme/         Color, Typography, Shape, AppTheme (dark + light)
│   └── ui/components/ AppScaffold, SideNavBar, TopBar, LabeledTextField,
│                      SectionCard, StatusBadge, ErrorBanner, ConfirmDialog …
│
└── feature/
    ├── wellinput/     domain · data · presentation · di
    ├── simulation/    domain · data · presentation · di
    ├── charts2d/      (in progress)
    ├── viewer3d/      (in progress)
    ├── reports/       (in progress)
    ├── dashboard/     (in progress)
    └── settings/      (in progress)
```

---

## Getting Started

### Prerequisites

- JDK 17 or higher
- IntelliJ IDEA 2023.3+ (recommended) or any Gradle-compatible IDE

### Clone & Run

```bash
git clone https://github.com/your-username/WellLogAnalyzer.git
cd WellLogAnalyzer
./gradlew run
```

### Build Native Installer

```bash
# Windows (.msi)
./gradlew packageMsi

# Linux (.deb)
./gradlew packageDeb
```

The output will be in `build/compose/binaries/`.

### Import a Well Profile from Excel

A Python script is included to generate a ready-to-import `.xlsx` file with realistic sample data:

```bash
pip install openpyxl
python scripts/generate_well_profile.py
# → generates Well_07A_Profile.xlsx
```

Then use **Import from Excel** in the Well Input screen to load it.

---

## Hydraulics Calculations

All equations follow **Bourgoyne et al. — Applied Drilling Engineering (SPE Vol. 2)** and **API RP 13D** in field units.

| Calculation | Formula |
|---|---|
| Hydrostatic pressure | `P = 0.052 × MW × TVD` |
| Annular velocity | `va = q / (2.448 × (d₁² - d₂²))` ft/min |
| Bingham laminar APL | `dP/dL = PV·va / (60000·dh²) + YP / (200·dh)` |
| Bingham turbulent APL | `dP/dL = 0.00000518 × ρ⁰·⁸ × va¹·⁸ × PV⁰·² / dh¹·²` |
| ECD | `ECD = MW + APL / (0.052 × TVD)` |
| Bit pressure drop | `ΔP = ρ·q² / (12031 × Cd² × TFA²)` |
| Hydraulic horsepower | `HHP = ΔP·q / 1714` |
| HSI | `HSI = HHP / A_bit` |
| Impact force | `F = 0.000518 × ρ × q × Vn` |

---

## Data Flow

```
User fills Well Input  →  ValidateWellProfileUseCase
                       ↓  (all errors in one pass)
              SaveWellProfileUseCase  →  WellProfileProtoStore (disk)
                       ↓
              RunSimulationUseCase  →  HydraulicsEngineImpl
                       ↓  emits Flow<PressurePoint>  (live UI progress)
              SimulationViewModel  →  StateFlow<SimulationStatus>
                       ↓
              SimulationResult saved  →  SimulationProtoStore (disk)
                       ↓
              Charts2DViewModel  ←  BuildChartDataUseCase
              Viewer3DViewModel  ←  ComputeWellTrajectoryUseCase
              ReportViewModel    ←  ExportExcelReportUseCase / ExportWordReportUseCase
```

---

## Design System

The UI uses **Material Design 3** in a custom petroleum-engineering dark theme:

| Token | Color | Usage |
|---|---|---|
| Navy Deep | `#0D1B2A` | Background, sidebar |
| Dark Slate | `#1A2535` | Surface |
| Card Surface | `#212E42` | Cards, panels |
| Amber Gold | `#F4A917` | Primary CTA, active nav, highlights |
| Teal Safe | `#2EC4B6` | Safe status, success states |
| Coral Danger | `#E63946` | Alerts, validation errors |

---

## Contributing

This project is currently in active development as part of a petroleum engineering internship project. Contributions, issue reports, and suggestions are welcome.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add my feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the Apache License 2.0 — see [LICENSE](LICENSE) for details.

---

<div align="center">
Built with 🛢️ by <a href="https://github.com/your-username">Oussama Chatri</a>
<br/>
<sub>Jetpack Compose Desktop · Kotlin · Clean Architecture · API RP 13D</sub>
</div>