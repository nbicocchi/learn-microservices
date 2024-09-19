#!/bin/bash

# Create a review
echo "Review creation..."
response=$(curl --location '127.0.0.1:7003/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation CreateReviews { createReviews(input: { productId: 112, reviewId: 5, author: \"luca\", subject: \"good\", content: \"very good\" }) { productId } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Check if the "errors" field is present in the response
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante la creazione della recensione: $error_message"
    else
        # Extract JSON values manually
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)

        echo "Recensione creata con successo:"
        echo "ID Prodotto: $productId"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante la creazione della recensione. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
    exit 1
fi
echo -e ""

# Get reviews for a product
echo "Reviews retrieval..."
response=$(curl --location '127.0.0.1:7003/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query GetReviews { getReviews(productId: 112) { productId reviewId author subject content } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Check if the "errors" field is present in the response
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante il recupero delle recensioni: $error_message"
    else
        # Extract JSON values manually
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        reviewId=$(echo "$body" | grep -o '"reviewId":[0-9]*' | cut -d':' -f2)
        author=$(echo "$body" | grep -o '"author":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        subject=$(echo "$body" | grep -o '"subject":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        content=$(echo "$body" | grep -o '"content":"[^"]*"' | cut -d':' -f2 | tr -d '"')

        echo "Dettagli delle recensioni:"
        echo "ID Prodotto: $productId"
        echo "ID Recensione: $reviewId"
        echo "Autore: $author"
        echo "Soggetto: $subject"
        echo "Contenuto: $content"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante il recupero delle recensioni. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
    exit 1
fi
echo -e ""

# Try to create a review with the same ID
echo "Review creation with existing reviewId..."
response=$(curl --location '127.0.0.1:7003/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation CreateReviews { createReviews(input: { productId: 112, reviewId: 5, author: \"luca\", subject: \"good\", content: \"very good\" }) { productId } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Check if the "errors" field is present in the response
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante la creazione della recensione: $error_message"
        echo "----------TEST OK----------"
    else
        # Extract JSON values manually
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)

        echo "Recensione creata con successo:"
        echo "ID Prodotto: $productId"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante la creazione della recensione. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
    exit 1
fi
echo -e ""

# Clean up: try to delete the review
echo "Review deletion..."
response=$(curl --location '127.0.0.1:7003/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation { deleteReviews(productId: 112) }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Check if the "errors" field is present in the response
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante l'eliminazione della recensione: $error_message"
    else
        echo "Recensione eliminata con successo."
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante l'eliminazione della recensione. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
    exit 1
fi
echo -e ""

# Try to retrieve a review that does not exist
echo "Review retrieval for a non-existing review..."
response=$(curl --location '127.0.0.1:7003/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query GetReviews { getReviews(productId: 112) { productId reviewId author subject content } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Check if the "errors" field is present in the response
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante il recupero della recensione: $error_message"
        echo "----------TEST OK----------"
    else
        # Extract JSON values manually
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        reviewId=$(echo "$body" | grep -o '"reviewId":[0-9]*' | cut -d':' -f2)
        author=$(echo "$body" | grep -o '"author":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        subject=$(echo "$body" | grep -o '"subject":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        content=$(echo "$body" | grep -o '"content":"[^"]*"' | cut -d':' -f2 | tr -d '"')

        echo "Dettagli della recensione:"
        echo "ID Prodotto: $productId"
        echo "ID Recensione: $reviewId"
        echo "Autore: $author"
        echo "Soggetto: $subject"
        echo "Contenuto: $content"
    fi
else
    echo "Errore durante il recupero della recensione. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
    exit 1
fi
echo -e ""
