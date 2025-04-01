#!/bin/bash

find_slides_using_it() {
	for slide in *.md; do
		if grep "$1" "$slide" > /dev/null; then
			echo "$slide"
		fi
	done
}

check_extension() {
	for extension in $1; do
		[ "${2##*.}" == "$extension" ] && return 0
	done
	return 1
}

process_module() {
  echo "--->" "$1"
  cd "$1"

  for fname in images/*; do
    # drawio image
    if check_extension "drawio" "$fname"; then
      continue
    fi

    # useless image
    slides=$(find_slides_using_it "$fname")
    if [ -z "$slides" ]; then
      echo "[NU]" "$fname"
      rm -rf "$fname"
      continue
    fi

    # already webp image
    if check_extension "webp" "$fname"; then
      continue
    fi

    # convert to webp
    fname_webp="${fname%.*}".webp
    echo "[OK]" "$fname"
    magick "$fname" -quality 60 -define webp:lossless=false "$fname_webp"
    rm -rf "$fname"

    # update slides
    for slide in "$slides"; do
      sed -i s/$(basename "$fname")/$(basename "$fname_webp")/g "$slide"
    done
  done

  cd ../..
}

for file in *; do
  if [ -d "$file"/slides/images ]; then
    process_module "$file"/slides
  else
    echo skipping "$file"...
  fi
done

