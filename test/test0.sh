#!/bin/bash
curl -X POST -H "Content-Type: application/json" -d @user.json --cookie-jar cookiejar localhost:9080/login
