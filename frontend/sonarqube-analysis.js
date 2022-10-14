const sonarqubeScanner =  require('sonarqube-scanner');
sonarqubeScanner(
    {
        serverUrl:  'https://csse-s302g8.canterbury.ac.nz/sonarqube/',
        token: "60259269851cc2133f7f2f0f88aa6c7e14b479f9",
        options : {
            'sonar.projectKey': 'team-800-client',
            'sonar.projectName': 'Team 800 - Client',
            "sonar.sourceEncoding": "UTF-8",
            'sonar.sources': 'src',
            'sonar.tests': 'src',
            'sonar.inclusions': '**',
            'sonar.test.inclusions': 'src/**/*.spec.js,src/**/*.test.js,src/**/*.test.ts',
            'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.javascript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml'
        }
    }, () => {});