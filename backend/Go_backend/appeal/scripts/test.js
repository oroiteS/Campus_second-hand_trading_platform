const fs = require('fs');
const https = require('https');
const http = require('http');

// 配置
const BASE_URL = 'http://localhost:8093'; // 根据实际情况修改端口
const OUTPUT_FILE = 'response.txt';

// 测试数据（基于用户提供的数据）
const testData = {
    // 订单数据
    orders: [
        {
            order_id: '01234567-89ab-7def-9123-456789abcde1',
            commodity_id: '01234567-89ab-7def-8123-456789abcde1',
            buyer_id: 'S20210004',
            seller_id: 'S20210002',
            order_status: 'completed',
            sale_time: '2025-06-18 20:31:59',
            money: 120.00,
            buy_quantity: 1
        }
    ],
    // 用户数据
    users: [
        {
            user_id: 'S20210004',
            user_name: '刘小强',
            password: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
            telephone: '13845678901',
            real_name: '刘强',
            avatar_url: 'https://example.com/avatar4.jpg',
            user_loc_longitude: 121.894585,
            user_sta: false,
            create_at: '2025-06-18 14:37:35',
            id: '310101199904041237',
            user_loc_latitude: 30.904871
        },
        {
            user_id: 'S20210002',
            user_name: '李小红',
            password: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
            telephone: '13823456789',
            real_name: '李红',
            avatar_url: 'https://example.com/avatar2.jpg',
            user_loc_longitude: 121.892585,
            user_sta: false,
            create_at: '2025-06-18 14:37:35',
            id: '310101199902021235',
            user_loc_latitude: 30.902871
        }
    ],
    // 管理员数据
    admin: {
        root_id: 'ADMIN0001',
        password: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855'
    }
};

// 日志函数
function log(message) {
    const timestamp = new Date().toISOString();
    const logMessage = `[${timestamp}] ${message}\n`;
    fs.appendFileSync(OUTPUT_FILE, logMessage);
    console.log(logMessage.trim());
}

// HTTP请求函数
function makeRequest(method, path, data = null, headers = {}) {
    return new Promise((resolve, reject) => {
        const url = new URL(BASE_URL + path);
        const options = {
            hostname: url.hostname,
            port: url.port || 80,
            path: url.pathname + url.search,
            method: method,
            headers: {
                'Content-Type': 'application/json',
                ...headers
            }
        };

        if (data) {
            const jsonData = JSON.stringify(data);
            options.headers['Content-Length'] = Buffer.byteLength(jsonData);
        }

        const req = http.request(options, (res) => {
            let responseData = '';
            res.on('data', (chunk) => {
                responseData += chunk;
            });
            res.on('end', () => {
                try {
                    const parsedData = JSON.parse(responseData);
                    resolve({
                        status: res.statusCode,
                        data: parsedData
                    });
                } catch (e) {
                    resolve({
                        status: res.statusCode,
                        data: responseData
                    });
                }
            });
        });

        req.on('error', (err) => {
            reject(err);
        });

        if (data) {
            req.write(JSON.stringify(data));
        }
        req.end();
    });
}

// 测试函数
async function testAPI() {
    // 清空输出文件
    fs.writeFileSync(OUTPUT_FILE, '');

    log('='.repeat(80));
    log('申诉模块 API 测试开始');
    log('='.repeat(80));

    let createdAppealId = null;

    try {
        // 1. 测试创建申诉
        log('\n1. 测试创建申诉 API');
        log('-'.repeat(50));
        const createAppealData = {
            argue1Id: testData.users[0].user_id,
            argue2Id: testData.users[1].user_id,
            orderId: testData.orders[0].order_id,
            reason: '商品与描述不符，要求退款'
        };

        const createResponse = await makeRequest('POST', '/api/v1/appeals', createAppealData);
        log(`请求: POST /api/v1/appeals`);
        log(`请求数据: ${JSON.stringify(createAppealData, null, 2)}`);
        log(`响应状态: ${createResponse.status}`);
        log(`响应数据: ${JSON.stringify(createResponse.data, null, 2)}`);

        if (createResponse.data && createResponse.data.argumentId) {
            createdAppealId = createResponse.data.argumentId;
            log(`创建的申诉ID: ${createdAppealId}`);
        }

        // 2. 测试通过订单ID查询申诉状态
        log('\n2. 测试通过订单ID查询申诉状态 API');
        log('-'.repeat(50));
        const getStatusResponse = await makeRequest('GET', `/api/v1/appeals/order/${testData.orders[0].order_id}`);
        log(`请求: GET /api/v1/appeals/order/${testData.orders[0].order_id}`);
        log(`响应状态: ${getStatusResponse.status}`);
        log(`响应数据: ${JSON.stringify(getStatusResponse.data, null, 2)}`);

        // 3. 测试批量查询申诉状态
        log('\n3. 测试批量查询申诉状态 API');
        log('-'.repeat(50));
        const batchOrderIds = {
            orderIds: [ // 修复字段名
                testData.orders[0].order_id,
                '01234567-89ab-7def-9999-456789abcde9',
                '01234567-89ab-7def-8888-456789abcde8'
            ]
        };

        const batchResponse = await makeRequest('POST', '/api/v1/appeals/batch-status', batchOrderIds);
        log(`请求: POST /api/v1/appeals/batch-status`);
        log(`请求数据: ${JSON.stringify(batchOrderIds, null, 2)}`);
        log(`响应状态: ${batchResponse.status}`);
        log(`响应数据: ${JSON.stringify(batchResponse.data, null, 2)}`);

        // 4. 测试取消申诉
        if (createdAppealId) {
            log('\n4. 测试取消申诉 API');
            log('-'.repeat(50));
            const cancelResponse = await makeRequest('PUT', `/api/v1/appeals/${createdAppealId}/cancel`, null, {
                'User-ID': testData.users[0].user_id // 添加用户ID header
            });
            log(`请求: PUT /api/v1/appeals/${createdAppealId}/cancel`);
            log(`响应状态: ${cancelResponse.status}`);
            log(`响应数据: ${JSON.stringify(cancelResponse.data, null, 2)}`);
        } else {
            log('\n4. 跳过取消申诉测试（未获取到申诉ID）');
        }

        // 5. 测试查询不存在的订单申诉状态
        log('\n5. 测试查询不存在的订单申诉状态');
        log('-'.repeat(50));
        const nonExistentOrderId = '01234567-89ab-7def-9999-456789abcde9';
        const nonExistentResponse = await makeRequest('GET', `/api/v1/appeals/order/${nonExistentOrderId}`);
        log(`请求: GET /api/v1/appeals/order/${nonExistentOrderId}`);
        log(`响应状态: ${nonExistentResponse.status}`);
        log(`响应数据: ${JSON.stringify(nonExistentResponse.data, null, 2)}`);

        // 管理员API测试（放在最后）
        log('\n' + '='.repeat(80));
        log('管理员 API 测试');
        log('='.repeat(80));

        // 6. 测试管理员更新申诉状态
        if (createdAppealId) {
            log('\n6. 测试管理员更新申诉状态 API');
            log('-'.repeat(50));
            const adminUpdateData = {
                status: 'finish',
                rootId: testData.admin.root_id // 修复字段名
            };

            const adminUpdateResponse = await makeRequest('PUT', `/api/v1/appeals/${createdAppealId}/admin-update`, adminUpdateData);
            log(`请求: PUT /api/v1/appeals/${createdAppealId}/admin-update`);
            log(`请求数据: ${JSON.stringify(adminUpdateData, null, 2)}`);
            log(`响应状态: ${adminUpdateResponse.status}`);
            log(`响应数据: ${JSON.stringify(adminUpdateResponse.data, null, 2)}`);
        } else {
            log('\n6. 跳过管理员更新申诉状态测试（未获取到申诉ID）');
        }

        // 7. 测试管理员更新不存在的申诉
        log('\n7. 测试管理员更新不存在的申诉');
        log('-'.repeat(50));
        const nonExistentAppealId = '01234567-89ab-7def-9999-456789abcde9';
        const adminUpdateNonExistentData = {
            status: 'refuse',
            rootId: testData.admin.root_id // 修复字段名
        };

        const adminUpdateNonExistentResponse = await makeRequest('PUT', `/api/v1/appeals/${nonExistentAppealId}/admin-update`, adminUpdateNonExistentData);
        log(`请求: PUT /api/v1/appeals/${nonExistentAppealId}/admin-update`);
        log(`请求数据: ${JSON.stringify(adminUpdateNonExistentData, null, 2)}`);
        log(`响应状态: ${adminUpdateNonExistentResponse.status}`);
        log(`响应数据: ${JSON.stringify(adminUpdateNonExistentResponse.data, null, 2)}`);

    } catch (error) {
        log(`测试过程中发生错误: ${error.message}`);
        log(`错误堆栈: ${error.stack}`);
    }

    log('\n' + '='.repeat(80));
    log('申诉模块 API 测试完成');
    log('='.repeat(80));
    log(`测试结果已保存到: ${OUTPUT_FILE}`);
}

// 运行测试
if (require.main === module) {
    testAPI().then(() => {
        console.log('测试完成，请查看 response.txt 文件获取详细结果');
    }).catch((error) => {
        console.error('测试失败:', error);
    });
}

module.exports = { testAPI, makeRequest, log };