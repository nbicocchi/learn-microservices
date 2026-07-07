#!/bin/bash
set -e

# 1. Config for Mermaid
echo '{"args": ["--no-sandbox", "--disable-setuid-sandbox"]}' > puppeteer-config.json

# 2. Config for Standard A4 Document
echo '{ "pdf_options": { "format": "A4", "margin": "10mm" } }' > config-standard.json

# 3. Config for Continuous Vertical Scroll
echo '{ "pdf_options": { "format": "", "width": "210mm", "height": "5000mm", "margin": "10mm", "printBackground": true } }' > config-scroll.json

if [ "$EVENT_NAME" = "workflow_dispatch" ]; then
  FOLDERS=$(find . -type f -name "*.md" ! -path '*/.*' ! -path '*/node_modules/*' -exec dirname {} \; | sort -u)
else
  FOLDERS="$CHANGED_FOLDERS"
fi

for folder in $FOLDERS; do
  [ -z "$folder" ] && continue
  echo "Processing folder: $folder"

  # --- 1. MERMAID TO IMAGES ---
  mkdir -p "$folder/images"
  shopt -s nullglob
  for file in "$folder"/*.md; do
    filename=$(basename "$file")
    safename=$(echo "$filename" | tr ' ' '_')
    mmdc -i "$file" -o "$folder/$safename" -p puppeteer-config.json -e png -s 4
    if [ "$filename" != "$safename" ]; then
      mv "$folder/$safename" "$file"
    fi
  done
  shopt -u nullglob

  mv "$folder"/*.png "$folder/images/" 2>/dev/null || true
  sed -i 's|\](\./|\](images/|g' "$folder"/*.md 2>/dev/null || true

  # --- 2. MARKDOWN TO PDF (DUAL GENERATION) ---
  rm -rf "$folder/pdf-standard" "$folder/pdf-scroll"
  mkdir -p "$folder/pdf-standard" "$folder/pdf-scroll"

  shopt -s nullglob
  for file in "$folder"/*.md; do
    echo "Converting $file..."
    filename=$(basename "$file" .md)

    # --- NEW: PROCESS DYNAMIC HTML COVER PER FILE ---
    HAS_COVER=false
    COVER_PDF="$folder/compiled_cover.pdf"
    if [ -f "$folder/cover.html" ]; then
      echo "Compiling dynamic cover for $filename..."
      HAS_COVER=true

      # Replace LESSON_TITLE with the actual filename and output to a temp file
      sed "s/LESSON_TITLE/$filename/g" "$folder/cover.html" > "$folder/temp_cover.html"

      # Run Puppeteer against the temporary HTML file
      node .github/build-assets/render-cover.js "$folder/temp_cover.html" "$COVER_PDF"
    fi

    # A. Generate Standard Layout
    md-to-pdf --config-file config-standard.json --launch-options='{"args": ["--no-sandbox", "--disable-setuid-sandbox"]}' "$file"
    if [ -f "$folder/$filename.pdf" ]; then
      if [ "$HAS_COVER" = true ]; then
        qpdf --empty --pages "$COVER_PDF" 1-z "$folder/$filename.pdf" 1-z -- "$folder/pdf-standard/$filename.pdf"
        rm "$folder/$filename.pdf"
      else
        mv "$folder/$filename.pdf" "$folder/pdf-standard/"
      fi
    fi

    # B. Generate Continuous Scroll Layout
    md-to-pdf --config-file config-scroll.json --launch-options='{"args": ["--no-sandbox", "--disable-setuid-sandbox"]}' "$file"
    if [ -f "$folder/$filename.pdf" ]; then
      if [ "$HAS_COVER" = true ]; then
        qpdf --empty --pages "$COVER_PDF" 1-z "$folder/$filename.pdf" 1-z -- "$folder/pdf-scroll/$filename.pdf"
        rm "$folder/$filename.pdf"
      else
        mv "$folder/$filename.pdf" "$folder/pdf-scroll/"
      fi
    fi

    # Cleanup temporary cover files generated for this specific Markdown file
    if [ "$HAS_COVER" = true ]; then
      rm -f "$folder/temp_cover.html" "$COVER_PDF"
    fi

  done
  shopt -u nullglob
done