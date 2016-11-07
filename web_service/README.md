This Node.js project intends to be the web service that receives information
about the usage of the CardioDroID android application but also will be able
to let users share data with other users.

## Running the server app

1) You can launch the app from the Terminal:

    $ npm start

Once the server is running...

## WEB Service Documentation

# USERS
POST /api/users/    -> Criar novo Utilizador

    body:{email: <email>, name: <string>}		

    response: (204)

GET /api/users/{user_email}/group    -> Get the group to which the User belongs
    
    response: {"name": <group name>}

# GROUPS											
GET /api/groups/                       -> Obter todos os grupos.
    
    response: {"groups": [{"name":<group name>}, ... ]}
    
POST /api/groups/    -> Criar novo grupo.
    
    body: {name: <group name>}
    response: (204)
    
POST /api/groups/{group_name}/users/{user_email}    -> Adicionar utilizador ao grupo.
    
    body:<empty>
    response: (204)

# RULES
GET /api/rules/{user_email}                            -> Obter as regras pertinentes a um utilizador.
    
    response: {"rules": [{"id": <integer>, "creator": <email>, "jsonRule": <json object>, "isPrivate": <boolean>}, ...]}

POST /api/rules/{user_email}                           -> Adicionar uma nova Regra para o utilizador.

    body: {"id": <integer>, "jsonRule": <json object>, "isPrivate": <boolean>}
    reponse: (201 - Location Header set) {"id": <integer>}

# Logs
GET /api/logs                            -> Obter todos os Logs.
    
    response: {"logs": [{"id": <integer>, "user_email": <email>, "date":<date>, "contexts": <json object>}, ...]}

POST /api/logs                           -> Adicionar um novo Log.

    body: {"user": <email>, "date":<date>, "contexts": <json object>}
    reponse: (204)