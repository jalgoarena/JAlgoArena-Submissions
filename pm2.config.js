module.exports = {
    apps: [
        {
            name: 'submissions',
            args: [
                "-jar",
                "build/libs/jalgoarena-submissions-2.0.0-SNAPSHOT.jar"
            ],
            script: 'java',
            env: {
                PORT: 5004,
                BOOTSTRAP_SERVERS: 'localhost:9092,localhost:9093,localhost:9094',
                EUREKA_URL: 'http://localhost:5000/eureka/'
            }
        }
    ]
};
