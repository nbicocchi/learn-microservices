#!/bin/bash
# Script: random_divisors_latency.sh
# Descrizione: chiama l'endpoint /divisors con n casuale e misura latenza

# Parametri
MIN_N=1              # valore minimo di n
MAX_N=100            # valore massimo di n
TIMES=10000000       # numero di iterazioni
FAULTS=0             # percentuale di errori simulati
URL="http://127.0.0.1:8080/divisors"

echo "Chiamata | n | Latenza (s) | Response"

for i in $(seq 1 100); do
  # Genera n casuale tra MIN_N e MAX_N
  N=$((RANDOM % (MAX_N - MIN_N + 1) + MIN_N))

  # Effettua la chiamata GET e misura la latenza
  LATENCY=$(curl -s -o /dev/null -w "%{time_total}" "$URL?n=$N&times=$TIMES&faults=$FAULTS")

  # Effettua anche la richiesta normale per vedere il risultato
  RESPONSE=$(curl -s "$URL?n=$N&times=$TIMES&faults=$FAULTS")

  echo "[$i] | $N | $LATENCY | $RESPONSE"
done