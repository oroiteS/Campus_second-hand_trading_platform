const fs = require('fs');
const path = require('path');

// 测试数据
const testData = {
    commodity: {
        id: '01234567-89ab-7def-8123-456789abcde0',
        name: '高等数学教材',
        description: '同济大学第七版，几乎全新，无笔记',
        price: 45.00,
        condition: '9成新'
    },
    user: {
        id: 'S20210001',
        name: '张小明',
        phone: '13812345678',
        realName: '张明'
    }
};

// API基础URL
const BASE_URL = 'http://localhost:8091/api/v1';

// 输出文件路径
const OUTPUT_FILE = path.join(__dirname, 'response.txt');

// 日志函数
function log(message) {
    const timestamp = new Date().toISOString();
    const logMessage = `[${timestamp}] ${message}\n`;
    fs.appendFileSync(OUTPUT_FILE, logMessage);
}

// 清空输出文件
function clearOutputFile() {
    if (fs.existsSync(OUTPUT_FILE)) {
        fs.unlinkSync(OUTPUT_FILE);
    }
}

// HTTP请求函数
async function makeRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        const data = await response.json();
        return {
            status: response.status,
            data: data
        };
    } catch (error) {
        return {
            status: 0,
            error: error.message
        };
    }
}

// 测试创建评论
async function testCreateComment() {
    log('=== 测试创建评论 ===');

    const commentData = {
        commodity_id: testData.commodity.id,
        user_id: testData.user.id,
        message: '这本书质量很好，内容清晰，推荐购买！'
    };

    const result = await makeRequest(`${BASE_URL}/comments`, {
        method: 'POST',
        body: JSON.stringify(commentData)
    });

    log(`创建评论请求: ${JSON.stringify(commentData, null, 2)}`);
    log(`创建评论响应: ${JSON.stringify(result, null, 2)}`);

    if (result.status === 200 && result.data.data) {
        return result.data.data.message_id;
    }
    return null;
}

// 测试创建回复
async function testCreateReply(parentMessageId) {
    log('\n=== 测试创建回复 ===');

    const replyData = {
        commodity_id: testData.commodity.id,
        user_id: 'T20210003', // 假设这是另一个用户
        message: '谢谢推荐，我也觉得这本书很不错！',
        reply_to_message_id: parentMessageId
    };

    const result = await makeRequest(`${BASE_URL}/comments`, {
        method: 'POST',
        body: JSON.stringify(replyData)
    });

    log(`创建回复请求: ${JSON.stringify(replyData, null, 2)}`);
    log(`创建回复响应: ${JSON.stringify(result, null, 2)}`);

    if (result.status === 200 && result.data.data) {
        return result.data.data.message_id;
    }
    return null;
}

// 测试获取评论列表
async function testGetComments() {
    log('\n=== 测试获取评论列表 ===');

    const params = new URLSearchParams({
        commodity_id: testData.commodity.id,
        page: 1,
        page_size: 10,
        sort_by: 'created_at',
        order: 'desc'
    });

    const result = await makeRequest(`${BASE_URL}/comments?${params}`);

    log(`获取评论列表请求: GET ${BASE_URL}/comments?${params}`);
    log(`获取评论列表响应: ${JSON.stringify(result, null, 2)}`);

    return result;
}

// 测试获取评论详情
async function testGetCommentDetail(messageId) {
    log('\n=== 测试获取评论详情 ===');

    const result = await makeRequest(`${BASE_URL}/comments/${messageId}`);

    log(`获取评论详情请求: GET ${BASE_URL}/comments/${messageId}`);
    log(`获取评论详情响应: ${JSON.stringify(result, null, 2)}`);

    return result;
}

// 测试删除评论
async function testDeleteComment(messageId) {
    log('\n=== 测试删除评论 ===');

    const params = new URLSearchParams({
        user_id: testData.user.id
    });

    const result = await makeRequest(`${BASE_URL}/comments/${messageId}?${params}`, {
        method: 'DELETE'
    });

    log(`删除评论请求: DELETE ${BASE_URL}/comments/${messageId}?${params}`);
    log(`删除评论响应: ${JSON.stringify(result, null, 2)}`);

    return result;
}

// 测试错误情况
async function testErrorCases() {
    log('\n=== 测试错误情况 ===');

    // 测试无效的商品ID
    log('\n--- 测试无效商品ID ---');
    const invalidCommodityData = {
        commodity_id: 'invalid-commodity-id',
        user_id: testData.user.id,
        message: '测试无效商品ID'
    };

    const invalidResult = await makeRequest(`${BASE_URL}/comments`, {
        method: 'POST',
        body: JSON.stringify(invalidCommodityData)
    });

    log(`无效商品ID请求: ${JSON.stringify(invalidCommodityData, null, 2)}`);
    log(`无效商品ID响应: ${JSON.stringify(invalidResult, null, 2)}`);

    // 测试空消息内容
    log('\n--- 测试空消息内容 ---');
    const emptyMessageData = {
        commodity_id: testData.commodity.id,
        user_id: testData.user.id,
        message: ''
    };

    const emptyResult = await makeRequest(`${BASE_URL}/comments`, {
        method: 'POST',
        body: JSON.stringify(emptyMessageData)
    });

    log(`空消息内容请求: ${JSON.stringify(emptyMessageData, null, 2)}`);
    log(`空消息内容响应: ${JSON.stringify(emptyResult, null, 2)}`);

    // 测试获取不存在的评论详情
    log('\n--- 测试获取不存在的评论 ---');
    const notFoundResult = await makeRequest(`${BASE_URL}/comments/non-existent-id`);

    log(`获取不存在评论请求: GET ${BASE_URL}/comments/non-existent-id`);
    log(`获取不存在评论响应: ${JSON.stringify(notFoundResult, null, 2)}`);
}

// 测试健康检查
async function testHealthCheck() {
    log('\n=== 测试健康检查 ===');

    const result = await makeRequest('http://localhost:8091/health');

    log(`健康检查请求: GET http://localhost:8091/health`);
    log(`健康检查响应: ${JSON.stringify(result, null, 2)}`);

    return result;
}

// 主测试函数
async function runTests() {
    clearOutputFile();

    log('开始评论功能测试');
    log(`测试时间: ${new Date().toISOString()}`);
    log(`测试数据: ${JSON.stringify(testData, null, 2)}`);
    log('\n' + '='.repeat(50));

    try {
        // 1. 健康检查
        await testHealthCheck();

        // 2. 创建评论
        const commentMessageId = await testCreateComment();

        if (commentMessageId) {
            // 3. 创建回复
            const replyMessageId = await testCreateReply(commentMessageId);

            // 4. 获取评论列表
            await testGetComments();

            // 5. 获取评论详情
            await testGetCommentDetail(commentMessageId);

            if (replyMessageId) {
                await testGetCommentDetail(replyMessageId);
            }

            // 6. 测试错误情况
            await testErrorCases();

            // 7. 删除评论（最后执行）
            await testDeleteComment(commentMessageId);

            // 8. 验证删除后获取评论
            log('\n=== 验证删除后获取评论 ===');
            await testGetCommentDetail(commentMessageId);
        } else {
            log('创建评论失败，跳过后续测试');
        }

    } catch (error) {
        log(`测试过程中发生错误: ${error.message}`);
        log(`错误堆栈: ${error.stack}`);
    }

    log('\n' + '='.repeat(50));
    log('评论功能测试完成');
    log(`结果已保存到: ${OUTPUT_FILE}`);
}

// 如果直接运行此脚本
if (require.main === module) {
    // 检查Node.js版本是否支持fetch
    if (typeof fetch === 'undefined') {
        log('错误: 当前Node.js版本不支持fetch API');
        log('请使用Node.js 18+版本，或安装node-fetch包');
        log('安装命令: npm install node-fetch');
        process.exit(1);
    }

    runTests().then(() => {
        console.log(`测试完成，结果已保存到 ${OUTPUT_FILE}`);
    }).catch(error => {
        console.error('测试失败:', error);
        process.exit(1);
    });
}

module.exports = {
    runTests,
    testCreateComment,
    testCreateReply,
    testGetComments,
    testGetCommentDetail,
    testDeleteComment,
    testErrorCases,
    testHealthCheck
};