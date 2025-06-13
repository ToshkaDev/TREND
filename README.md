## TREND - a highly customizable computational platform for protein analysis

![License](https://img.shields.io/github/license/ToshkaDev/TREND)
![Maven](https://img.shields.io/badge/build-maven-blue.svg)
![Requirements](https://img.shields.io/badge/requirements-python%202.7%2B-blue)
![Top Language](https://img.shields.io/github/languages/top/ToshkaDev/TREND)
![Code size](https://img.shields.io/github/languages/code-size/ToshkaDev/TREND)
[![Web Server](https://img.shields.io/badge/Live%20Server-trend.evobionet.com-brightgreen)](https://trend.evobionet.com/)
[![Published Article DOI](https://zenodo.org/badge/DOI/10.1093/nar/gkaa243.svg)](https://doi.org/10.1093/nar/gkaa243)
[![Correction Article DOI](https://zenodo.org/badge/DOI/10.1093/nar/gkac034.svg)](https://doi.org/10.1093/nar/gkac034)
![Maintenance](https://img.shields.io/badge/maintenance-active-brightgreen.svg)

TREND (**TR**ee-based **E**xploration of **N**eighborhoods and **D**omains) is a modular, web-based platform for automated analysis of protein function in prokaryotes, integrating phylogenetic relationships, domain architectures, and gene neighborhood context. The platform provides ample opportunities for adjusting each step of the analysis.

The live web server is freely available at: https://trend.evobionet.com

TREND streamlines the complex workflows typically required for:
- Multiple sequence alignment and phylogenetic tree construction
- Identification of protein features (e.g., Pfam/CDD domains, TM regions, low-complexity regions)
-  Gene neighborhood and operon prediction
- Mapping functional annotations and genomic context onto evolutionary trees
- Cross-referencing with RefSeq, Pfam, CDD and MiST databases
- All results are presented in an interactive visualization, with export options for downstream analysis (JSON, SVG, FASTA, Newick)

### Features
- Fully automated pipelines for domain architecture and gene neighborhood analysis
- Input via protein sequences, identifiers (NCBI, RefSeq, MiST), multiple sequence alignments, phylogenetic trees
- Integration with Pfam, CDD, and MiST databases
- Protein feature identification using Profile Hidden Markov models from multiple databses: CDD, Pfam (InterPro), COG, KOG, SMART, PRK, TIGRFAMs, MiST
- Sequence edundancy reduction with CD-HIT and multiple sequence alignment using MAFFT algorithms 
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

### Citation
If you use TREND in your work, please cite:

_Vadim M. Gumerov, Igor B. Zhulin (2020). TREND: a platform for exploring protein function in prokaryotes based on phylogenetic, domain architecture and gene neighborhood analyses. [Nucleic Acids Research, 48: W72–W76](https://doi.org/10.1093/nar/gkaa243)._

_Vadim M. Gumerov, Igor B. Zhulin (2022). Correction (URL change) to ‘TREND: a platform for exploring protein function in prokaryotes based on phylogenetic, domain architecture and gene neighborhood analyses’. [Nucleic Acids Research, 50: 1795](https://doi.org/10.1093/nar/gkac034)._

