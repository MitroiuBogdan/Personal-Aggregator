 curl -X POST http://admin:admin123@localhost:3000/api/datasources \
      -H 'Content-Type: application/json' \
      -d '{
            "name": "InfluxDB",
            "type": "influxdb",
            "access": "proxy",
            "url": "http://influxdb:8086",
            "jsonData": {
              "version": "Flux",
              "organization": "yllu",
              "defaultBucket": "yllu"
            },
            "secureJsonData": {
              "token": "nVePJnSrUJrU4lEtoSUcoEIPMvCo_0Dxw-JIL2pbak-vukX4XQJpzflevHvUPxJTOwgXnwBFMc36MLNXJtYH_Q=="
            }
          }
          '