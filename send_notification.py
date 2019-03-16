import sys
import requests
import json

line = sys.argv[2] + '\n' + sys.argv[3] + '\n' + sys.argv[4] + '\n' + sys.argv[5] + '\n' + sys.argv[6] + '\n' + sys.argv[7]

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
			"body":line,
			"content_available":"true"
		},
	"to":""
}


headers = {
	"Content-Type":"application/json",
	"Authorization":"key="
}

requests.post(url, data = json.dumps(body), headers = headers)
