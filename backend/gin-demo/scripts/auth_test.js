//此api用于测试注册、登录、获取token、访问受保护的api
const http = require('http');
const fs = require('fs');
const path = require('path');

const logFilePath = path.join(__dirname, 'response.txt');

// 清空或创建日志文件
fs.writeFileSync(logFilePath, '');

function log(message) {
    console.log(message); // 仍然在控制台输出，方便调试
    fs.appendFileSync(logFilePath, message + '\n', 'utf8');
}

async function makeRequest(options, postData) {
    return new Promise((resolve, reject) => {
        log(`\n----- Request -----`);
        log(`Method: ${options.method}`);
        log(`Path: ${options.path}`);
        if (options.headers) {
            log(`Headers: ${JSON.stringify(options.headers)}`);
        }
        if (postData) {
            log(`Body: ${JSON.stringify(postData)}`);
        }

        const req = http.request(options, (res) => {
            let responseBody = '';
            log(`\n----- Response -----`);
            log(`Status: ${res.statusCode}`);
            log(`Headers: ${JSON.stringify(res.headers)}`);

            res.setEncoding('utf8');
            res.on('data', (chunk) => {
                responseBody += chunk;
            });
            res.on('end', () => {
                log(`Body: ${responseBody}`);
                try {
                    resolve({ statusCode: res.statusCode, headers: res.headers, body: JSON.parse(responseBody) });
                } catch (e) {
                    log(`Error parsing JSON response: ${e.message}`)
                    resolve({ statusCode: res.statusCode, headers: res.headers, body: responseBody }); // 返回原始body
                }
            });
        });

        req.on('error', (e) => {
            log(`\n----- Error -----`);
            log(`Error: ${e.message}`);
            reject(e);
        });

        if (postData) {
            req.write(JSON.stringify(postData));
        }
        req.end();
    });
}

async function runTests() {
    const baseURL = 'localhost';
    const port = 8080;
    let jwtToken = '';

    // --- 1. 注册新用户 ---
    log('\n########### Test 1: Register New User ###########');
    const registerData = {
        userid: `testuser${Date.now()}`.slice(0, 15), // 确保userid唯一且长度符合要求
        username: 'Test User For Script',
        password: 'password123',
        role: 'user'
    };
    try {
        const registerResponse = await makeRequest({
            hostname: baseURL,
            port: port,
            path: '/api/auth/register',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        }, registerData);
        if (registerResponse.statusCode !== 201) {
            log(`Registration failed with status: ${registerResponse.statusCode}`);
        }
    } catch (error) {
        log(`Error during registration: ${error.message}`);
    }

    // --- 2. 使用刚注册的用户登录 ---
    log('\n########### Test 2: Login with Registered User ###########');
    const loginData = {
        identifier: registerData.userid, // 使用上一步注册的 userid
        password: registerData.password
    };
    try {
        const loginResponse = await makeRequest({
            hostname: baseURL,
            port: port,
            path: '/api/auth/login',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        }, loginData);

        if (loginResponse.statusCode === 200 && loginResponse.body.token) {
            jwtToken = loginResponse.body.token;
            log(`Logged in successfully. Token: ${jwtToken}`);
        } else {
            log(`Login failed with status: ${loginResponse.statusCode}`);
        }
    } catch (error) {
        log(`Error during login: ${error.message}`);
    }

    // --- 3. 尝试注册一个已存在的 UserID ---
    log('\n########### Test 3: Register Existing UserID ###########');
    try {
        const existingRegisterData = {
            userid: registerData.userid, // 使用已存在的 UserID
            username: 'Another Test User',
            password: 'password456',
            role: 'user'
        };
        await makeRequest({
            hostname: baseURL,
            port: port,
            path: '/api/auth/register',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        }, existingRegisterData);
    } catch (error) {
        log(`Error during existing user registration test: ${error.message}`);
    }

    // --- 4. 尝试使用错误的密码登录 ---
    log('\n########### Test 4: Login with Wrong Password ###########');
    const wrongLoginData = {
        identifier: registerData.userid,
        password: 'wrongpassword'
    };
    try {
        await makeRequest({
            hostname: baseURL,
            port: port,
            path: '/api/auth/login',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        }, wrongLoginData);
    } catch (error) {
        log(`Error during wrong password login test: ${error.message}`);
    }

    // --- 5. 访问受保护的端点 (如果已获取到token) ---
    if (jwtToken) {
        log('\n########### Test 5: Access Protected Endpoint ###########');
        try {
            await makeRequest({
                hostname: baseURL,
                port: port,
                path: '/api/protected/data',
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`,
                }
            });
        } catch (error) {
            log(`Error accessing protected endpoint: ${error.message}`);
        }
    } else {
        log('\nSkipping Test 5: Access Protected Endpoint (No token obtained)');
    }

    // --- 6. 访问受保护的端点 (没有token) ---
    log('\n########### Test 6: Access Protected Endpoint Without Token ###########');
    try {
        await makeRequest({
            hostname: baseURL,
            port: port,
            path: '/api/protected/data',
            method: 'GET',
            headers: {}
        });
    } catch (error) {
        log(`Error accessing protected endpoint without token: ${error.message}`);
    }

    log('\n\nAll tests completed. Check response.txt for details.');
}

runTests();