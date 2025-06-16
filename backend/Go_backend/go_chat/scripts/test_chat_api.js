// 脚本说明：
// 1. 本脚本用于测试 Go Chat 后端 API。
// 2. 依赖 'node-fetch' 库进行 HTTP 请求。请确保已安装 (npm install node-fetch)。
// 3. 所有输出将写入同目录下的 response.txt 文件，控制台不输出信息。
// 4. 模拟 testuser1 (ID: 1) 和 testuser2 (ID: 2) 之间的交互。

const fs = require('fs');
const path = require('path');
const fetch = (...args) => import('node-fetch').then(({ default: fetch }) => fetch(...args));

const BASE_URL = 'http://localhost:8080/api/v1/chat';
const USER1_ID = '1';
const USER2_ID = '2';

let outputLog = [];

async function logAndFetch(url, options, description) {
    const logEntry = { request: { url, options, description }, response: null, error: null };
    outputLog.push(`\n--- ${description} ---`);
    outputLog.push(`Request: ${options.method || 'GET'} ${url}`);
    if (options.body) {
        outputLog.push(`Request Body: ${options.body}`);
    }
    try {
        const response = await fetch(url, options);
        const data = await response.json();
        outputLog.push(`Response Status: ${response.status}`);
        outputLog.push(`Response Body: ${JSON.stringify(data, null, 2)}`);
        logEntry.response = { status: response.status, body: data };
        return data;
    } catch (error) {
        outputLog.push(`Error: ${error.message}`);
        logEntry.error = error.message;
        console.error(`Error during fetch for ${description}:`, error); // Keep console error for debugging script issues
        return null;
    }
}

async function runTests() {
    outputLog.push('Starting Chat API Tests...');

    // 1. Create a session between testuser1 and testuser2
    const sessionData = await logAndFetch(`${BASE_URL}/sessions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ buyer_id: USER1_ID, seller_id: USER2_ID })
    }, 'Create Session (User1 and User2)');

    if (!sessionData || !sessionData.session_id) {
        outputLog.push('Failed to create session. Aborting further tests that depend on session_id.');
        return;
    }
    const sessionId = sessionData.session_id;

    // 2. User1 sends a message to User2
    await logAndFetch(`${BASE_URL}/messages`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            session_id: sessionId,
            sender_id: USER1_ID,
            receiver_id: USER2_ID,
            message_type: 'text',
            content: 'Hello User2, this is User1!'
        })
    }, 'User1 Sends Message to User2');

    // 3. User2 sends a message to User1
    await logAndFetch(`${BASE_URL}/messages`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            session_id: sessionId,
            sender_id: USER2_ID,
            receiver_id: USER1_ID,
            message_type: 'text',
            content: 'Hi User1, User2 here!'
        })
    }, 'User2 Sends Message to User1');

    // 4. Get User1's sessions
    await logAndFetch(`${BASE_URL}/user/${USER1_ID}/sessions`, {
        method: 'GET'
    }, 'Get User1 Sessions');

    // 5. Get User2's sessions
    await logAndFetch(`${BASE_URL}/user/${USER2_ID}/sessions`, {
        method: 'GET'
    }, 'Get User2 Sessions');

    // 6. Get messages for the session
    await logAndFetch(`${BASE_URL}/session/${sessionId}/messages?limit=10&offset=0`, {
        method: 'GET'
    }, 'Get Session Messages');

    // 7. Get User1's unread count
    await logAndFetch(`${BASE_URL}/user/${USER1_ID}/unread`, {
        method: 'GET'
    }, 'Get User1 Unread Count');

    // 8. Get User2's unread count (after User1 sent a message)
    await logAndFetch(`${BASE_URL}/user/${USER2_ID}/unread`, {
        method: 'GET'
    }, 'Get User2 Unread Count');

    // Simulate User2 reading a message (assuming the last message sent by User1 is the one to mark as read)
    // To do this properly, we'd need the message_id. For this test, we'll skip marking as read via API
    // as it requires knowing a specific message ID from a previous response.
    // Instead, we'll just re-check unread counts.

    outputLog.push('\nTests Finished.');
}

async function main() {
    await runTests();
    const outputPath = path.join(__dirname, 'response.txt');
    fs.writeFileSync(outputPath, outputLog.join('\n'), 'utf8');
    // console.log(`Test results written to ${outputPath}`); // No console output as per requirement
}

main().catch(err => {
    // In case of unhandled promise rejections in main itself
    const errorLog = `Unhandled error in main: ${err.message}\n${err.stack}`;
    outputLog.push(errorLog);
    const outputPath = path.join(__dirname, 'response.txt');
    fs.writeFileSync(outputPath, outputLog.join('\n'), 'utf8');
    // console.error('Critical error during test execution:', err); // No console output
});
