#!flask/bin/python
from flask import Flask, jsonify, abort, make_response, request
from polyglot.text import Text

app = Flask(__name__)


@app.route('/todo/api/v1.0/tasks', methods=['POST'])
def create_task():
    count=int(request.data[0])
    lang = request.data[1:count+1]
    
    request.data
    res = Text(request.data[count+1:], hint_language_code=lang)


    
    aList = []
    for sent in res.sentences:
       #for entity in sent.entities:
         aList.append({'sentence': [(e.tag, e) for e in sent.entities]})
    output = {'entities':aList}    
    return jsonify(output), 201

if __name__ == '__main__':
    app.run()
