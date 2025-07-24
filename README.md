## TREND - a highly customizable computational platform for protein analysis

![License](https://img.shields.io/github/license/ToshkaDev/TREND)
![Maven](https://img.shields.io/badge/build-maven-blue.svg)
[![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=flat&logo=docker&logoColor=white)](https://www.docker.com/)
![Top Language](https://img.shields.io/github/languages/top/ToshkaDev/TREND)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Web Server](https://img.shields.io/badge/Live%20Server-trend.evobionet.com-brightgreen)](https://trend.evobionet.com/)
[![Published Article DOI](https://zenodo.org/badge/DOI/10.1093/nar/gkaa243.svg)](https://doi.org/10.1093/nar/gkaa243)
[![Correction Article DOI](https://zenodo.org/badge/DOI/10.1093/nar/gkac034.svg)](https://doi.org/10.1093/nar/gkac034)
![Maintenance](https://img.shields.io/badge/maintenance-active-brightgreen.svg)
<!-- [![Docker Pulls](https://img.shields.io/docker/pulls/bioliners/trend.svg)](https://hub.docker.com/r/bioliners/trend) -->

TREND (**TR**ee-based **E**xploration of **N**eighborhoods and **D**omains) is a modular, web-based platform for automated analysis of protein function in prokaryotes, integrating phylogenetic relationships, domain architectures, and gene neighborhood context. The platform provides ample opportunities for adjusting each step of the analysis.

The live web server is freely available at: https://trend.evobionet.com

TREND streamlines the complex workflows typically required for:
- Multiple sequence alignment and phylogenetic tree construction
- Identification of protein features (e.g., Pfam/CDD domains, TM regions, low-complexity regions)
- Gene neighborhood and operon prediction
- Mapping functional annotations and genomic context onto evolutionary trees
- Cross-referencing with RefSeq, Pfam, CDD and MiST databases
- All results are presented in an interactive visualization, with export options for downstream analysis (JSON, SVG, FASTA, Newick)

### Features
- Fully automated pipelines for domain architecture and gene neighborhood analysis
- Input via protein sequences, identifiers (NCBI, RefSeq, MiST), multiple sequence alignments, phylogenetic trees
- Integration with Pfam, CDD, and MiST databases
- Protein feature identification using Profile Hidden Markov models from multiple databses: CDD, Pfam (InterPro), COG, KOG, SMART, PRK, TIGRFAMs, MiST
- Sequence redundancy reduction with CD-HIT and multiple sequence alignment using MAFFT algorithms 
- Phylogenetic tree building using FastTree and MEGA
- Gene neighborhoods and opreron identification are avilable for over 200,000 genomes
- Interactive visual outputs linking evolutionary and functional contexts
- Web interface and downloadable results

### Tech Stack
- Backend: Java (Spring Boot, Hibernate)
- Frontend: Thymeleaf, jQuery, Bootstrap, D3.js, HTML5/CSS
- Database: PostgreSQL
- Web Server: Tomcat with NGINX reverse proxy
- Integrations: Python scripts for bioinformatics tasks

### General Requirements
- [git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### Setup
```bash
$ git clone https://github.com/ToshkaDev/TREND.git
$ cd TREND
```

### Getting Started
- **Build the development Docker image**: `make build-dev`

This Makefile target builds the development image, which includes application dependencies, tools, and multiple preconfigured protein domain databases. The process typically takes **15–18 minutes** on a standard internet connection due to the volume of packages, the size of the protein databases that need to be downloaded and prepared, and the complexity of the Docker layers involved.

- **Start the application in development mode**: `make up-dev`

This command spins up the full development stack via Docker Compose using `docker-compose.yml`. It launches the backend and frontend services along with a database container. Once running, the main application will be available at: http://localhost:8080. Make sure you have an `.env` file in the root directory before starting the services. A fully prepared `.env` file is already available in this repository.

Use dbshell.sh script to connect to the running database: 
```bash
$ chmod 755 dbshell.sh
$ ./dbshell.sh
```

- **To stop the development server, run**: `make down-dev`

This command stops and removes the containers defined in `docker-compose.yml`. However, depending on your system and how the containers were started, some containers may persist. For a full teardown, ensure you're running it from the same directory where the stack was started, and consider using `--remove-orphans` or `docker system prune`. To stop and fully clean the development environment (including volumes), run: `make clean-dev`

- **Use the prebuilt production image (optional)**: `docker pull bioliners/trend:v1.5.0`

To avoid building locally, you can pull the latest production image from Docker Hub. This image is fully built, optimized for runtime, and does not include development tools or hot-reloading features.

### Citation
If you use TREND in your work, please cite:

_Vadim M. Gumerov, Igor B. Zhulin (2020). TREND: a platform for exploring protein function in prokaryotes based on phylogenetic, domain architecture and gene neighborhood analyses. [Nucleic Acids Research, 48: W72–W76](https://doi.org/10.1093/nar/gkaa243)._

_Vadim M. Gumerov, Igor B. Zhulin (2022). Correction (URL change) to ‘TREND: a platform for exploring protein function in prokaryotes based on phylogenetic, domain architecture and gene neighborhood analyses’. [Nucleic Acids Research, 50: 1795](https://doi.org/10.1093/nar/gkac034)._

