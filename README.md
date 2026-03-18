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

## Prerequisites

You need **JDK 17 or higher** installed. Check with:

```bash
java -version
```

If not installed:

| OS | Command |
|---|---|
| **Windows** | Download from [adoptium.net](https://adoptium.net) and run the installer |
| **macOS** | `brew install temurin@17` or download from [adoptium.net](https://adoptium.net) |
| **Linux** | `sudo apt install openjdk-17-jdk` / `sudo pacman -S jdk17-openjdk` / `sudo dnf install java-17-openjdk` |

---

## Quick Start — Run from Source

Works on **Windows, macOS, and Linux**:

```bash
git clone https://github.com/your-username/WellLogAnalyzer.git
cd WellLogAnalyzer
```

| OS | Command |
|---|---|
| **Linux / macOS** | `./gradlew run` |
| **Windows** | `gradlew.bat run` or `.\gradlew run` in PowerShell |

The app will launch with an animated splash screen, then open to the Dashboard.

---

## Quick Test — Import a Sample Well in 2 Minutes

Rather than filling in all drilling parameters manually, use the included Python script to generate a ready-made Excel profile and import it into the app.

### Step 1 — Install Python (if needed)

| OS | How |
|---|---|
| **Windows** | Download from [python.org](https://python.org) — tick "Add to PATH" during install |
| **macOS** | `brew install python` or download from [python.org](https://python.org) |
| **Linux** | Usually pre-installed. If not: `sudo apt install python3` / `sudo pacman -S python` |

Check it works: `python --version` or `python3 --version`

### Step 2 — Install openpyxl

```bash
# Windows
pip install openpyxl

# macOS / Linux (if python3 is the default)
pip3 install openpyxl
```

### Step 3 — Generate the Excel file

The script `generate_well_profile.py` is at the **project root** (same folder as `build.gradle.kts`).

```bash
# Windows
python generate_well_profile.py

# macOS / Linux
python3 generate_well_profile.py
```

This creates `Well_07A_Profile.xlsx` in the same folder. You'll see:

```
✓ Generated: Well_07A_Profile.xlsx

── Well Summary ─────────────────────────────────────────────
  Well        : Well-07A
  Total Depth : 9500.0 ft
  Mud Weight  : 10.5 ppg  |  Flow Rate : 400.0 gpm
  PV / YP     : 18.0 cP / 12.0 lb/100ft²
  Formations  : 5 zones  (0 → 9500.0 ft)
  Survey pts  : 11  (max inclination 45°)
────────────────────────────────────────────────────────────
```

> **Customise it:** open `generate_well_profile.py` and edit `WELL_INFO`, `FLUID_PROPS`, `FORMATIONS`, and `SURVEY` at the top. Re-run the script to regenerate.

### Step 4 — Import into WellLogAnalyzer

1. Open the app and go to **Well Input**
2. Click **Import from Excel** in the top bar
3. Select `Well_07A_Profile.xlsx`
4. Click **Validate** → all checks should turn green
5. Click **Run Simulation →**

That's it. The full Well-07A profile loads instantly and you can run the simulation, view 2D charts, explore the 3D viewer, and export reports.

---

## Building a Native Installer

### Linux

Run directly on your machine:

```bash
# AppImage — single file, runs on any Linux distro without installation
./gradlew packageAppImage
# → build/compose/binaries/main/app/WellLogAnalyzer-1.0.0.AppImage

# .deb package — for Debian / Ubuntu based systems
./gradlew packageDeb
# → build/compose/binaries/main/deb/wellloganalyzer_1.0.0_amd64.deb
```

To run the AppImage:
```bash
chmod +x WellLogAnalyzer-1.0.0.AppImage
./WellLogAnalyzer-1.0.0.AppImage
```

---

### Windows

> You must run this on a Windows machine or via GitHub Actions (see below). The `packageExe` task uses WiX Toolset which only runs on Windows.

**On a Windows machine:**
```powershell
gradlew.bat packageExe
# → build\compose\binaries\main\exe\WellLogAnalyzer-1.0.0.exe

gradlew.bat packageMsi
# → build\compose\binaries\main\msi\WellLogAnalyzer-1.0.0.msi
```

**Via GitHub Actions (recommended — no Windows machine needed):**

Push your code to GitHub and create `.github/workflows/build.yml` — see the [GitHub Actions section](#building-all-platforms-with-github-actions) below. After the workflow runs, download the `WellLogAnalyzer-Windows` artifact from the Actions tab. It contains both the `.exe` and `.msi`.

---

### macOS

> You must run this on a Mac or via GitHub Actions. The `packageDmg` task uses Apple's native `hdiutil` which only runs on macOS.

**Via GitHub Actions (recommended):**

Same workflow as Windows — after the run, download the `WellLogAnalyzer-macOS` artifact from the Actions tab. It contains the `.dmg`.

**On a Mac directly:**
```bash
./gradlew packageDmg
# → build/compose/binaries/main/dmg/WellLogAnalyzer-1.0.0.dmg
```

---

### Any Platform — Fat JAR (no installation needed)

If you just want something that runs everywhere without a native installer:

```bash
./gradlew packageUberJarForCurrentOS
# → build/compose/jars/WellLogAnalyzer-<os>-x64-1.0.0.jar

# Run it — requires JDK 17+ on the target machine
java -jar WellLogAnalyzer-linux-x64-1.0.0.jar
```

---

### Summary Table

| Installer | Build on | Command |
|---|---|---|
| Linux AppImage | Linux | `./gradlew packageAppImage` |
| Linux .deb | Linux | `./gradlew packageDeb` |
| Windows .exe | Windows or GitHub Actions | `gradlew.bat packageExe` |
| Windows .msi | Windows or GitHub Actions | `gradlew.bat packageMsi` |
| macOS .dmg | macOS or GitHub Actions | `./gradlew packageDmg` |
| Fat JAR (all OS) | Any | `./gradlew packageUberJarForCurrentOS` |

---

## Building All Platforms with GitHub Actions

Create `.github/workflows/build.yml` in your repo. On every push to `main`, GitHub will build Linux, Windows, and macOS installers in parallel for free:

```yaml
name: Build Native Installers

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:

  build-linux:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build Linux packages
        run: ./gradlew packageAppImage packageDeb
      - uses: actions/upload-artifact@v4
        with:
          name: WellLogAnalyzer-Linux
          path: |
            build/compose/binaries/main/app/
            build/compose/binaries/main/deb/

  build-windows:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build Windows installers
        run: ./gradlew packageExe packageMsi
      - uses: actions/upload-artifact@v4
        with:
          name: WellLogAnalyzer-Windows
          path: |
            build/compose/binaries/main/exe/
            build/compose/binaries/main/msi/

  build-mac:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build macOS DMG
        run: ./gradlew packageDmg
      - uses: actions/upload-artifact@v4
        with:
          name: WellLogAnalyzer-macOS
          path: build/compose/binaries/main/dmg/
```

After pushing: go to your repo → **Actions** tab → click the latest run → scroll to **Artifacts** → download.

---

## Features

| Feature | Status | Description |
|---|---|---|
| **Splash Screen** | ✅ Complete | Animated launch screen — rotating drill-bit ring, ECD-gradient wellbore tube, amber/teal progress bar. Cross-fades to main UI after ~3.6 s. |
| **Well Input** | ✅ Complete | Multi-tab parameter entry — drill string, bit, fluid properties, formation zones, deviation survey. Full validation + Excel import. |
| **Hydraulics Engine** | ✅ Complete | Depth-step solver emitting a live `Flow<PressurePoint>`. Bingham Plastic & Power Law rheology. ECD, APL, bit hydraulics, surge/swab. |
| **Simulation Screen** | ✅ Complete | Three-panel layout — run controls, live ECD mini-chart with progress, scrollable monospace log. Results summary with metric cards. |
| **2D Charts** | ✅ Complete | Pressure-Depth, ECD Profile, Annular Velocity, Bit Hydraulics bar chart, Component Breakdown — all with safe-window shading and legend. |
| **3D Viewer** | ✅ Complete | Interactive wellbore + geology viewer. ECD colour-mapped tube, formation layer cylinders, camera controls. |
| **Report Export** | ✅ Complete | One-click Excel (.xlsx) and Word (.docx) engineering reports with cover page, KPI strip, and data tables. |
| **Dashboard** | ✅ Complete | Project overview with stat cards, recent wells table, quick actions, activity log, and simulations bar chart. ProtoBuf persistence. |
| **Settings** | ✅ Complete | 4 built-in themes, unit system selector, decimal precision, file path pickers. |
| **Theme System** | ✅ Complete | `WellLogColors` CompositionLocal — theme changes recompose the entire app instantly. Persisted across sessions. |
| **Data Persistence** | ✅ Complete | All well profiles, simulation results, and settings saved locally via `kotlinx-serialization-protobuf`. |

---

## Themes

4 built-in themes switchable from the palette icon in the top bar or from **Settings**. Persisted to disk.

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
| Excel profile generator | Python + openpyxl | 3.x / 3.1+ |

---

## Architecture

Clean Architecture — strict three-layer vertical slice per feature:

```
feature/
  domain/        ← Pure Kotlin. Zero framework dependencies.
  data/          ← Implements domain interfaces. Files, POI, Jzy3D, ProtoBuf.
  presentation/  ← Compose UI + ViewModel. Observes StateFlow, emits callbacks only.
```

Dependency rule enforced strictly: `presentation → domain ← data`

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