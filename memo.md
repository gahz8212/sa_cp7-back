# Advanced Excel Architecture Design

## 1. Goal
- **Flexibility**: Store any Excel format without schema changes using JSON.
- **Visual Freedom**: Support complex multi-headers and styling via Jxls templates.
- **Efficiency**: Hybrid upload processing (Front/Back-end Smart Switching).
- **Integrity**: Validate data in a staging area before moving to normalized domain tables.
- **Automation**: Auto-generate base templates to reduce manual coding.

## 2. System Architecture

### A. Data Storage Layer
- **`excel_data` (Staging Table)**: 
    - Temporary JSON storage for raw uploaded data.
    - Used for preview, CRUD, and validation before final migration.
- **`excel_template` (File/Table)**: 
    - Stores Jxls `.xlsx` template files.
    - Manages versions of templates (auto-generated base vs. user-customized styling).
- **Domain Tables (Permanent)**: 
    - Manually designed normalized tables for core business logic.
    - Data is migrated here after validation from the staging layer.

### B. Smart Upload Switching (Hybrid)
- **Front-end Parsing (Client)**: For small files (< 5MB or < 5,000 rows), parse to JSON via ExcelJS/SheetJS and send directly to the API.
- **Back-end Parsing (Server)**: For large files, stream the file to the server and parse using Apache POI (SXSSF) to avoid memory overflow.

## 3. Key Workflows

### Phase 1: Upload & Staging
1. **Upload**: User uploads a file (restricted to 1st-row header for simplicity in auto-parsing).
2. **Header Detection**: System identifies headers (Default: Row 0). User can override the header range in the staging UI.
3. **Storage**: Data is stored in `excel_data` as JSON.
4. **Auto-Template Generation**: 
    - System extracts headers and creates a base `.xlsx` file.
    - Injects Jxls tags (e.g., `${item.c1}, ${item.c2}`) into the data row.
    - Saves the file to the `excel_template` repository.

### Phase 2: Validation & Migration
1. **CRUD**: Users can edit/delete rows directly in the staging UI (modifying JSON).
2. **Validation**: Run business rules against the staging data.
3. **Mapping**: Define mapping between JSON keys (`c1`, `c2`) and Domain Table columns.
4. **Migration**: Move clean data to the manually created Domain Tables.

### Phase 3: Styled Download
1. **Customization**: 
    - Download the auto-generated base template.
    - Modify it in Excel (add 2-tier headers, colors, merging, logos).
    - Re-upload (replace) the template.
2. **Rendering**:
    - Download request fetches the specified Jxls template.
    - Jxls engine injects DB data into the template.
    - If no template exists, fallback to raw data export with basic styling.

## 4. Technology Stack
- **Back-end**: Spring Boot 3, Apache POI (SXSSF), **Jxls**, MyBatis.
- **Front-end**: **ExcelJS** (Client parsing/generation), **Luckysheet/FortuneSheet** (UI Preview).
- **Database**: PostgreSQL/MySQL with JSONB/JSON support.

## 5. Pros & Cons
- **Pros**:
    - No Java code required for complex styling (handled by Excel templates).
    - Scalable for massive datasets.
    - High data quality via staging/validation process.
- **Cons**:
    - Requires management of template files.
    - Initial setup of mapping logic is required for each domain.
