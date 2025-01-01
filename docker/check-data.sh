curl --request POST "http://localhost:8086/api/v2/query?org=yllu" \
  --header "Authorization: Token nVePJnSrUJrU4lEtoSUcoEIPMvCo_0Dxw-JIL2pbak-vukX4XQJpzflevHvUPxJTOwgXnwBFMc36MLNXJtYH_Q==" \
  --header "Content-Type: application/vnd.flux" \
  --data '{
    "query": "from(bucket: \"yllu\") |> range(start: -1h)"
  }'
