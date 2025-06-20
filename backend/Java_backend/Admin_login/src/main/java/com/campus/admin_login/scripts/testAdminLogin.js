// 定义登录接口的URL
const loginUrl = 'http://localhost:8080/api/admin/login';

// 定义测试用的管理员凭据
const adminCredentials = {
    rootId: 'ADMIN0003', // 替换为实际的管理员ID
    password: '123456' // 替换为实际的密码
};

// 定义一个异步函数来发送登录请求
async function testAdminLogin(credentials) {
    try {
        console.log('发送登录请求:', credentials);
        const response = await fetch(loginUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(credentials)
        });

        // 检查响应状态码
        if (!response.ok) {
            // 如果状态码不是2xx，则尝试解析错误信息
            let errorData;
            try {
                errorData = await response.json();
            } catch (e) {
                // 如果无法解析JSON，则使用文本内容
                errorData = await response.text();
            }
            console.error('登录失败:', response.status, response.statusText, errorData);
            return { success: false, status: response.status, data: errorData };
        }

        // 解析JSON响应体
        const data = await response.json();
        console.log('登录成功:', data);
        return { success: true, status: response.status, data: data };

    } catch (error) {
        console.error('请求过程中发生错误:', error);
        return { success: false, error: error.message };
    }
}

// 执行测试
async function runTests() {
    console.log('--- 开始管理员登录测试 ---');

    // 测试1: 使用正确的凭据登录
    console.log('\n--- 测试1: 正确凭据 ---');
    const correctLoginResult = await testAdminLogin(adminCredentials);
    if (correctLoginResult.success && correctLoginResult.data.code === 200) {
        console.log('测试1 通过: 成功登录');
    } else {
        console.error('测试1 失败:', correctLoginResult);
    }

    // 测试2: 使用错误的密码登录
    console.log('\n--- 测试2: 错误密码 ---');
    const wrongPasswordCredentials = { ...adminCredentials, password: 'wrongpassword' };
    const wrongPasswordResult = await testAdminLogin(wrongPasswordCredentials);
    if (!wrongPasswordResult.success && wrongPasswordResult.data && wrongPasswordResult.data.code === 400 && wrongPasswordResult.data.message === '用户名或密码错误') {
        console.log('测试2 通过: 用户名或密码错误提示正确');
    } else {
        console.error('测试2 失败:', wrongPasswordResult);
    }

    // 测试3: 使用不存在的管理员ID登录
    console.log('\n--- 测试3: 不存在的管理员ID ---');
    const nonExistentAdminCredentials = { rootId: 'nonexistentadmin', password: 'password123' };
    const nonExistentAdminResult = await testAdminLogin(nonExistentAdminCredentials);
    if (!nonExistentAdminResult.success && nonExistentAdminResult.data && nonExistentAdminResult.data.code === 400 && nonExistentAdminResult.data.message === '用户名或密码错误') {
        console.log('测试3 通过: 用户名或密码错误提示正确');
    } else {
        console.error('测试3 失败:', nonExistentAdminResult);
    }

    // 测试4: 不提供rootId
    console.log('\n--- 测试4: 缺少管理员ID ---');
    const missingRootIdCredentials = { password: 'password123' };
    const missingRootIdResult = await testAdminLogin(missingRootIdCredentials);
    // 根据你的后端验证逻辑，这里可能返回特定的错误信息或状态码
    // 假设后端会因为 @NotBlank 而返回400错误
    if (!missingRootIdResult.success && missingRootIdResult.status === 400) {
        console.log('测试4 通过: 缺少管理员ID时返回错误');
    } else {
        console.error('测试4 失败:', missingRootIdResult);
    }

    console.log('\n--- 管理员登录测试结束 ---');
}

// 运行测试函数
runTests();

// 如果你想在Node.js环境中运行，你可能需要安装node-fetch:
// npm install node-fetch
// 然后在脚本顶部添加: const fetch = require('node-fetch');