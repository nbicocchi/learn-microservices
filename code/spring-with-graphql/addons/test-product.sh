#!/bin/bash

# Create a product
echo "Product creation..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation { createProduct(input: { productId: 92, name: \"1111\", weight: 111 }) { productId name weight } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante la creazione del prodotto: $error_message"
    else
        # Estrai i valori JSON manualmente
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        name=$(echo "$body" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        weight=$(echo "$body" | grep -o '"weight":[0-9]*' | cut -d':' -f2)

        echo "Prodotto creato con successo:"
        echo "ID: $productId"
        echo "Nome: $name"
        echo "Peso: $weight"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante la creazione del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Get a product
echo "Product retrieval..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query GetProduct { getProduct(productId: 92) { productId name weight } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante il recupero del prodotto: $error_message"
    else
        # Estrai i valori JSON manualmente
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        name=$(echo "$body" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        weight=$(echo "$body" | grep -o '"weight":[0-9]*' | cut -d':' -f2)
        # serviceAddress=$(echo "$body" | grep -o '"serviceAddress":"[^"]*"' | cut -d':' -f2- | tr -d '"')

        echo "Dettagli del prodotto:"
        echo "ID: $productId"
        echo "Nome: $name"
        echo "Peso: $weight"
        # echo "Indirizzo del servizio: $serviceAddress"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante il recupero del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Try to create a product with the same ID
echo "Product creation with existing productId..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation { createProduct(input: { productId: 92, name: \"1111\", weight: 111 }) { productId name weight } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante la creazione del prodotto: $error_message"
        echo "----------TEST OK----------"
    else
        # Estrai i valori JSON manualmente
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        name=$(echo "$body" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        weight=$(echo "$body" | grep -o '"weight":[0-9]*' | cut -d':' -f2)

        echo "Prodotto creato con successo:"
        echo "ID: $productId"
        echo "Nome: $name"
        echo "Peso: $weight"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante la creazione del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Try to delete a product
echo "Product deletion..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation DeleteProduct { deleteProduct(productId: 92) }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante l'eliminazione del prodotto: $error_message"
    else
        echo "Prodotto eliminato con successo."
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante l'eliminazione del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Try to create a product with invalid data
echo "Product creation with invalid data..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation { createProduct(input: { productId: a, name: \"1111\", weight: aaa }) { productId name weight } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante la creazione del prodotto: $error_message"
        echo "----------TEST OK----------"
    else
        # Estrai i valori JSON manualmente
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        name=$(echo "$body" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        weight=$(echo "$body" | grep -o '"weight":[0-9]*' | cut -d':' -f2)

        echo "Prodotto creato con successo:"
        echo "ID: $productId"
        echo "Nome: $name"
        echo "Peso: $weight"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante la creazione del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Try to create a product with the same ID
echo "Product creation with same productId after deletion (it should work now)..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation { createProduct(input: { productId: 92, name: \"1111\", weight: 111 }) { productId name weight } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante la creazione del prodotto: $error_message"
    else
        # Estrai i valori JSON manualmente
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        name=$(echo "$body" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        weight=$(echo "$body" | grep -o '"weight":[0-9]*' | cut -d':' -f2)

        echo "Prodotto creato con successo:"
        echo "ID: $productId"
        echo "Nome: $name"
        echo "Peso: $weight"
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante la creazione del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Try to delete a product
echo "Product deletion to clean database..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation DeleteProduct { deleteProduct(productId: 92) }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante l'eliminazione del prodotto: $error_message"
    else
        echo "Prodotto eliminato con successo."
        echo "----------TEST OK----------"
    fi
else
    echo "Errore durante l'eliminazione del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""

# Try to retrieve a product that does not exist
echo "Product retrieval for a non-existing product..."
response=$(curl --location '127.0.0.1:7001/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query GetProduct { getProduct(productId: 92) { productId name weight } }"}' \
-w "\n%{http_code}" -s)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    # Controlla se il campo "errors" è presente nella risposta
    if grep -q '"errors":' <<< "$body"; then
        error_message=$(echo "$body" | grep -o '"message":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        echo "Errore durante il recupero del prodotto: $error_message"
        echo "----------TEST OK----------"
    else
        # Estrai i valori JSON manualmente
        productId=$(echo "$body" | grep -o '"productId":[0-9]*' | cut -d':' -f2)
        name=$(echo "$body" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')
        weight=$(echo "$body" | grep -o '"weight":[0-9]*' | cut -d':' -f2)
        # serviceAddress=$(echo "$body" | grep -o '"serviceAddress":"[^"]*"' | cut -d':' -f2- | tr -d '"')

        echo "Dettagli del prodotto:"
        echo "ID: $productId"
        echo "Nome: $name"
        echo "Peso: $weight"
        # echo "Indirizzo del servizio: $serviceAddress"
        # echo "----------TEST OK----------"
    fi
else
    # echo "----------TEST OK----------"
    echo "Errore durante il recupero del prodotto. Codice HTTP: $http_code"
    echo "Risposta del server:"
    echo "$body"
    echo "TEST FAILED"
        exit 1
fi
echo -e ""