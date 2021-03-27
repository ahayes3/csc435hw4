#!/bin/bash
curl --cookie-jar cookies -X PUT -H "Content-Type: application/json" -d @user.json localhost:9080/login
curl --cookie-jar cookies --cookie cookies -X GET localhost:9080/characters > out.json
