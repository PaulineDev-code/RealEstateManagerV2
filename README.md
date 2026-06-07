# Real Estate Manager

Real Estate Manager est une application Android de gestion immobilière conçue pour accompagner des agents dans la consultation, la création, la modification et la recherche de biens immobiliers.

Développée en Kotlin avec Jetpack Compose, Room et Hilt, l’application met en avant une approche offline-first, une interface adaptative téléphone/tablette et une architecture pensée pour rester lisible, testable et évolutive.

## Aperçu des fonctionnalités

### Consulter les biens immobiliers

L’application permet de consulter l’ensemble des biens enregistrés avec leurs informations essentielles : type de bien, prix, description, photo principale.

<img width="162" height="351" alt="Liste des biens immobiliers" src="https://github.com/user-attachments/assets/48913d73-d8e8-4d2c-8156-00124a1c3f8e" />

### Accéder au détail d’un bien

Chaque bien dispose d’un écran de détail regroupant les informations utiles à un agent immobilier : description, prix, surface, nombre de pièces, adresse, statut, médias, points d’intérêt à proximité et agent responsable.

<img width="162" height="351" alt="Détail d'un bien immobilier" src="https://github.com/user-attachments/assets/1850bad5-0ff0-4cf3-95a3-b42ffd67cef5" />


### Créer et modifier un bien

Les utilisateurs peuvent créer ou modifier un bien immobilier à travers un formulaire complet.  
La fiche peut être enrichie avec des photos, des vidéos, des points d’intérêt, un agent associé et des informations de mise en vente ou de vente.

<img width="162" height="351" alt="Ajouter un bien immobilier" src="https://github.com/user-attachments/assets/1b14cbc1-4fde-41cf-90d2-584b3a0450be" />      <img width="162" height="351" alt="Modifier un bien immobilier" src="https://github.com/user-attachments/assets/ee414553-41ad-4ed4-8f68-3b211eb51fc1" />  

### Rechercher avec des filtres avancés

La recherche multicritère permet de filtrer les biens selon plusieurs critères : type, prix, surface, nombre de pièces, médias disponibles, points d’intérêt, dates et agent responsable.

<img width="162" height="351" alt="Recherche multicritère" src="https://github.com/user-attachments/assets/7e85b3ee-97de-4d7e-b9d2-96eff9f5d64b" />

### Visualiser les biens sur une carte

L’application propose une vue cartographique permettant de localiser les biens immobiliers à partir de leur adresse et de visualiser leur répartition géographique.

<img width="162" height="351" alt="Carte avec biens immobiliers" src="https://github.com/user-attachments/assets/af8ee481-8600-4af6-a2ec-f48783c2ffac" />

### Utiliser une interface adaptée au téléphone et à la tablette

L’interface s’adapte à la taille de l’écran. Sur tablette, l’application peut afficher la liste des biens et le détail sélectionné côte à côte, afin d’exploiter au mieux l’espace disponible.

<img width="444" height="260" alt="Affichage tablette" src="https://github.com/user-attachments/assets/73fea5e7-06b4-4a32-99cf-1bc38d49da52" /> 

## Architecture et choix techniques

L’objectif est de séparer les responsabilités entre l’interface, la logique métier et l’accès aux données, tout en gardant une structure adaptée à un projet Android mono-module.

Les ViewModels exposent les états observés par les écrans Compose. Les use cases portent les actions métier, tandis que les repositories font le lien entre le domaine et les sources de données locales.

### Stack utilisée

| Domaine | Technologies |
|---|---|
| Langage | Kotlin |
| Interface | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose, Material 3 Adaptive |
| Architecture | MVVM, principes de Clean Architecture |
| Injection de dépendances | Hilt |
| Persistance locale | Room, SQLite |
| Asynchrone / état UI | Coroutines, StateFlow |
| Images / médias | Coil, Media3 |
| Carte et localisation | Google Maps, Maps Compose, Play Services Location |
| Tests | JUnit, Mockito, Robolectric, Turbine, Hilt Android Testing, Compose UI Tests |

### Organisation générale

```text
Presentation
├── Écrans Jetpack Compose
├── Composables réutilisables
├── ViewModels
└── UI states

Domain
├── Modèles métier
├── Use cases
└── Contrats des repositories

Data
├── Implémentations des repositories
├── Room database
├── DAOs
├── Entités locales
├── Relations entre entités
├── Mappers
├── Network monitor
└── ContentProvider

DI
└── Modules Hilt
```

### Choix techniques et fonctionnalités avancées

- **Approche offline-first** : les biens, agents, médias et points d’intérêt sont stockés localement avec Room, ce qui permet à l’application de rester exploitable sans connexion réseau.

- **Modèle de données relationnel** : un bien est associé à un agent, à une liste de médias et à plusieurs points d’intérêt via une table de jonction.

- **Recherche multicritère avancée** : la recherche combine plusieurs filtres optionnels : type de bien, prix, surface, nombre de pièces, nombre de photos ou vidéos, points d’intérêt, dates et agent responsable.

- **Interface adaptative** : l’application adapte sa navigation et ses écrans aux téléphones et tablettes, avec un affichage liste / détail sur les grands écrans.

- **Gestion explicite de l’état UI** : les écrans s’appuient sur des sealed UI states pour représenter les états de chargement, de succès et d’erreur.

- **Gestion réseau** : l’application observe l’état de connexion pour signaler les pertes réseau et relancer certaines mises à jour lorsque la connexion redevient disponible.

- **Géocodage différé des biens** : lorsqu’un bien est créé sans coordonnées disponibles, son adresse peut être résolue plus tard afin de compléter sa latitude et sa longitude.

- **Gestion des médias** : les biens peuvent être enrichis avec des photos et des vidéos, associées à la fiche du bien et persistées localement.

- **Formatage local des dates et des prix** : l’application adapte l’affichage de certaines informations, comme les dates et les montants, au format local de l’utilisateur.

- **Validation des formulaires** : les formulaires de création, modification et recherche s’appuient sur une validation progressive des champs pour limiter les entrées invalides.

- **ContentProvider en lecture seule** : certaines données immobilières sont exposées via un ContentProvider, avec notification des changements lorsque les tables Room concernées sont modifiées.

## Tests

Le projet contient des tests sur plusieurs couches de l’application :

* tests unitaires des use cases et de la logique métier ;
* tests de ViewModels et de gestion d’état ;
* tests de repositories avec base Room en mémoire ;
* tests instrumentés pour certains comportements Android spécifiques, notamment le ContentProvider et la synchronisation liée au réseau.

## Installation

### Prérequis

* JDK 17
* Android Studio
* Android SDK 36
* Une clé API Google Maps

### Configuration

Ajouter la clé Google Maps dans le fichier `local.properties` à la racine du projet :

```properties
MAPS_API_KEY=your_api_key_here
```

Lancer ensuite le projet depuis Android Studio sur un émulateur ou un appareil physique.

## Améliorations possibles

* Ajouter une synchronisation distante avec un backend
* Externaliser les taux de conversion monétaire au lieu d’utiliser des valeurs statiques
* Optimiser l’affichage de grandes listes de biens
* Raffiner la séparation des mappers pour rapprocher davantage le projet d’une Clean Architecture stricte

## Contexte

Ce projet a été réalisé dans le cadre d’un parcours de formation Android et retravaillé comme projet portfolio.

Il met en pratique des problématiques courantes du développement Android moderne : persistance locale, architecture en couches, interface adaptative, gestion d’état, recherche multicritère, géolocalisation, tests et intégration de composants Android comme le ContentProvider.
