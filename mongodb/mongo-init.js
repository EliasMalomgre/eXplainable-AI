/*
 * This file can be used to execute command on the MongoDb when it is initialized (eg. seeding or creating users) 
 */

/* Code for creating a user that should work source: 
https://stackoverflow.com/questions/42912755/how-to-create-a-db-for-mongodb-container-on-start-up */

/*db.createUser(
    {
        user: "Elias",
        pwd: "Elias123",
        roles: [
            {
                role: "readWrite",
                db: "XAI"
            }
        ]
    }
);*/
/*https://stackoverflow.com/questions/42912755/how-to-create-a-db-for-mongodb-container-on-start-up
https://docs.mongodb.com/manual/reference/method/
https://hub.docker.com/_/mongo
https://github.com/docker-library/mongo/pull/145
*/