import sys
import requests
import json

url = 'https://fcm.googleapis.com/fcm/send'

body = {
	"data":
		{
			"title":"mytitle",
			"body":"mybody",
			"url":"myurl"
		},
	"notification":
		{
			"title":sys.argv[1],
			"body":sys.argv[2],
			"content_available":"true"
		},
	"to":""
}


headers = {
	"Content-Type":"application/json",
	"Authorization":"key="
}

requests.post(url, data = json.dumps(body), headers = headers)
