import {ax1,instance} from "./axios";

// 商品相关API
// const API_BASE_URL = '';

/**
 * 获取最新发布的商品
 * @returns {Promise} 返回最新6个商品的数据
 */
export const getLatestCommodities = async () => {
  try {
    // 使用 axios 发送请求
    const response = await ax1.get(`/api-8087/commodities/latest`);
    
    // 直接获取解析后的 data（axios 自动处理 JSON 解析）
    const result = response.data;
    
    // 检查业务状态码
    if (result.code === 200) {
      // 返回商品列表数据
      return result.data;
    } else {
      throw new Error(result.message || '获取商品数据失败');
    }
  } catch (error) {
    console.error('获取最新商品失败:', error);
    
    // 区分错误类型（网络错误 vs 业务错误）
    if (error.response) {
      console.error('HTTP错误:', error.response.status);
    }
    
    throw error; // 向上抛出错误
  }
};
/**
 * 获取所有用户信息
 * @returns {Promise} 返回所有用户数据
 */
export const getAllUsers = async () => {
  try {
    const response = await ax1.get('/api/users/all'); //虚空api
    
    const result = response.data;
    
    if (result.code === 200) {
      return result.data;
    } else {
      throw new Error(result.message || '获取用户数据失败');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    throw error;
  }
};

/**
 * 将后端商品数据转换为前端显示格式
 * @param {Array} commodities 后端商品数据
 * @param {Array} users 用户数据（可选，用于获取卖家信息）
 * @returns {Array} 转换后的商品数据
 */
export const transformCommodityData = async (commodities, users = []) => {
  const transformedItems = commodities.map(async item => {
    // 查找对应的卖家信息
    console.log(users)
    const seller = users.find(user => user.userId === item.userId) || {};

    // 解析图片列表
    let imageList = [];
    try {
      if (item.imageList) {
        imageList = JSON.parse(item.imageList);
      }
    } catch (e) {
      console.warn('解析图片列表失败:', e);
    }
    
    // 计算发布时间距离现在的时间
    const timeAgo = calculateTimeAgo(item.createdAt);
    return {
      id: item.commodityId,
      name: item.commodityName,
      price: item.currentPrice,
      condition: item.newness || '成色良好', // 直接使用后端返回的newness字段
      location: extractLocation(item.commodityDescription),
      image: imageList.length > 0 ? imageList[0] : (item.mainImageUrl || '/测试图片.jpg'),
      timeAgo: timeAgo,
      sellerName: seller.userName || '未知用户',
      sellerSchool: extractSchool(seller.userName) || '未知学院',
      sellerAvatar: seller.avatarUrl || `https://via.placeholder.com/30x30/4CAF50/FFFFFF?text=${seller.userName?.charAt(0) || 'U'}`,
      description: item.commodityDescription,
      status: item.commodityStatus,
      quantity: item.quantity,
      createdAt: item.createdAt,
      updatedAt: item.updatedAt
    };
  });
  
  // 等待所有异步操作完成
  return await Promise.all(transformedItems);
};

/**
 * 计算时间差 - 显示具体时间
 * @param {string} dateTime 时间字符串
 * @returns {string} 具体时间描述
 */
function calculateTimeAgo(dateTime) {
  if (!dateTime) return '未知时间';
  
  const publishDate = new Date(dateTime);
  
  // 检查日期是否有效
  if (isNaN(publishDate.getTime())) {
    return '未知时间';
  }
  
  const now = new Date();
  const diffMs = now - publishDate;
  const diffHours = Math.floor(diffMs / 3600000);
  
  // 如果是今天发布的，显示具体时间
  if (diffHours < 24 && publishDate.toDateString() === now.toDateString()) {
    const hours = String(publishDate.getHours()).padStart(2, '0');
    const minutes = String(publishDate.getMinutes()).padStart(2, '0');
    return `今天 ${hours}:${minutes}`;
  }
  // 如果是昨天发布的
  else if (diffHours < 48) {
    const yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);
    if (publishDate.toDateString() === yesterday.toDateString()) {
      const hours = String(publishDate.getHours()).padStart(2, '0');
      const minutes = String(publishDate.getMinutes()).padStart(2, '0');
      return `昨天 ${hours}:${minutes}`;
    }
  }
  
  // 其他情况显示具体日期
  const month = String(publishDate.getMonth() + 1).padStart(2, '0');
  const day = String(publishDate.getDate()).padStart(2, '0');
  const hours = String(publishDate.getHours()).padStart(2, '0');
  const minutes = String(publishDate.getMinutes()).padStart(2, '0');
  
  return `${month}月${day}日 ${hours}:${minutes}`;
}

/**
 * 从描述中提取位置信息
 * @param {string} description 商品描述
 * @returns {string} 位置信息
 */
function extractLocation(description) {
  const locationPatterns = [
    /(东校区|西校区|南校区|北校区|中心校区)/,
    /(宿舍|图书馆|食堂|教学楼)/,
    /([东西南北]区)/
  ];
  
  for (const pattern of locationPatterns) {
    const match = description.match(pattern);
    if (match) {
      return match[1];
    }
  }
  
  return '校内';
}

/**
 * 从用户名中提取学院信息（这里是示例，实际可能需要其他方式）
 * @param {string} userName 用户名
 * @returns {string} 学院信息
 */
// eslint-disable-next-line no-unused-vars
function extractSchool(userName) {
  // 这里可以根据实际情况调整学院提取逻辑
  const schools = [
    '计算机学院', '数学学院', '物理学院', '化学学院', 
    '生物学院', '外语学院', '文学院', '历史学院',
    '音乐学院', '美术学院', '体育学院', '经济学院'
  ];
  
  return schools[Math.floor(Math.random() * schools.length)];
}

/**
 * 获取商品详情
 * @param {string} commodityId 商品ID
 * @returns {Promise} 返回商品详情数据
 */
export const getCommodityDetail = async (commodityId) => {
  try {
    // 使用 axios 发送请求
    const response = await ax1.get(`/api-8083/commodity/detail/${commodityId}`);
    
    // axios 直接返回解析后的 data，不需要 response.json()
    const result = response.data;
    
    // 调试用：显示商品名称而不是整个对象
    alert(`商品名称: ${result.data.commodityName}`);
    
    // 检查业务状态码
    if (result.code === 200) {
      return result.data; // 返回商品数据
    } else {
      throw new Error(result.message || '获取商品详情失败');
    }
  } catch (error) {
    console.error('获取商品详情失败:', error);
    throw error; // 向上抛出错误
  }
};

/**
 * 将后端商品详情数据转换为前端显示格式
 * @param {Object} commodityData 后端商品详情数据
 * @returns {Object} 转换后的商品数据
 */
export const transformCommodityDetailData = async (commodityData) => {
  // 处理图片列表 - 适配实际后端响应格式
  let imageList = [];
  try {
    if (commodityData.imageList) {
      // 如果imageList是数组格式
      if (Array.isArray(commodityData.imageList)) {
        imageList = commodityData.imageList.map(url => {
          return typeof url === 'string' ? url.replace(/`/g, '').trim() : url;
        });
      } 
      // 如果imageList是字符串格式
      else if (typeof commodityData.imageList === 'string') {
        // 检查是否是BLOB格式
        if (commodityData.imageList.includes('<<BLOB>>')) {
          console.warn('imageList是BLOB格式，无法解析');
          imageList = [];
        } else {
          // 尝试解析JSON字符串
          try {
            const parsed = JSON.parse(commodityData.imageList);
            imageList = Array.isArray(parsed) ? parsed.map(url => 
              typeof url === 'string' ? url.replace(/`/g, '').trim() : url
            ) : [];
          } catch (parseError) {
            // 如果JSON解析失败，尝试按逗号分割
            imageList = commodityData.imageList.split(',').map(url => 
              url.replace(/`/g, '').trim()
            ).filter(url => url.length > 0);
          }
        }
      }
      // 如果imageList是BLOB或其他格式，尝试转换为字符串再处理
      else {
        console.warn('imageList格式未知，尝试转换为字符串:', commodityData.imageList);
        const imageListStr = String(commodityData.imageList);
        if (imageListStr && imageListStr !== '[object Object]' && !imageListStr.includes('<<BLOB>>')) {
          try {
            const parsed = JSON.parse(imageListStr);
            imageList = Array.isArray(parsed) ? parsed : [];
          } catch (e) {
            // 如果解析失败，按逗号分割
            imageList = imageListStr.split(',').map(url => 
              url.replace(/`/g, '').trim()
            ).filter(url => url.length > 0);
          }
        }
      }
    }
  } catch (e) {
    console.warn('解析图片列表失败:', e);
    imageList = [];
  }
  
  // 构建图片数组
  const images = [];
  
  // 首先添加主图（如果存在）
  if (commodityData.mainImageUrl && 
      commodityData.mainImageUrl.trim() !== '' && 
      !commodityData.mainImageUrl.includes('<<BLOB>>')) {
    const cleanMainImage = commodityData.mainImageUrl.replace(/`/g, '').trim();
    images.push(cleanMainImage);
    console.log('添加主图作为第一张图片:', cleanMainImage);
  }
  
  // 然后添加imageList中的其他图片（排除与主图重复的）
  imageList.forEach(img => {
    const cleanImg = typeof img === 'string' ? img.replace(/`/g, '').trim() : img;
    if (cleanImg && cleanImg.length > 0 && !cleanImg.includes('<<BLOB>>')) {
      // 避免重复添加主图
      if (!images.includes(cleanImg)) {
        images.push(cleanImg);
      }
    }
  });
  
  console.log('最终图片数组:', images);
  
  // 格式化发布时间 - 显示具体时间
  const formatPublishTime = (dateTime) => {
    if (!dateTime) return '未知时间';
    
    const publishDate = new Date(dateTime);
    
    // 检查日期是否有效
    if (isNaN(publishDate.getTime())) {
      return '未知时间';
    }
    
    // 格式化为具体时间：YYYY年MM月DD日 HH:mm
    const year = publishDate.getFullYear();
    const month = String(publishDate.getMonth() + 1).padStart(2, '0');
    const day = String(publishDate.getDate()).padStart(2, '0');
    const hours = String(publishDate.getHours()).padStart(2, '0');
    const minutes = String(publishDate.getMinutes()).padStart(2, '0');
    
    return `${year}年${month}月${day}日 ${hours}:${minutes}`;
  };
  
  // 转换商品状态显示文本
  const getStatusText = (status) => {
    const statusMap = {
      'on_sale': '在售',
      'sold': '已售',
      'off_sale': '下架'
    };
    return statusMap[status] || '未知状态';
  };
  
  // 获取卖家信息
  let sellerInfo = {
    id: commodityData.sellerId,
    name: '卖家用户',
    avatar: 'https://via.placeholder.com/60x60/4CAF50/FFFFFF?text=卖'
  };
  
  try {
    if (commodityData.sellerId) {
      const userInfo = await getUserInfo(commodityData.sellerId);
      sellerInfo = {
        id: userInfo.userId,
        name: userInfo.userName,
        avatar: userInfo.avatarUrl || 'https://via.placeholder.com/60x60/4CAF50/FFFFFF?text=卖'
      };
    }
  } catch (error) {
    console.warn('获取卖家信息失败，使用默认信息:', error);
  }
  
  // 适配实际后端响应字段名
  return {
    id: commodityData.commodityId,
    name: commodityData.commodityName,
    price: commodityData.currentPrice,
    condition: commodityData.newness || '成色良好',
    publishTime: formatPublishTime(commodityData.createdAt),
    quantity: commodityData.quantity || 1,
    description: commodityData.commodityDescription || '',
    detailDescription: commodityData.commodityDescription ? 
      commodityData.commodityDescription.split('\n').filter(line => line.trim()) : 
      ['暂无详细描述'],
    images: images.length > 0 ? images : [], // 移除默认测试图片，让前端处理空图片情况
    status: getStatusText(commodityData.commodityStatus),
    sellerId: commodityData.sellerId,
    categoryName: commodityData.categoryName || commodityData.category,
    createdAt: commodityData.createdAt,
    updatedAt: commodityData.updatedAt,
    seller: sellerInfo
  };
};

/**
 * 获取用户信息
 * @param {string} userId 用户ID
 * @returns {Promise} 返回用户信息数据
 */
export const getUserInfo = async (userId) => {
  console.log('iiiuserid',userId)
  try {
    const response = await ax1.post('/api-8089/user/info', {
        userId: userId
    });
    
    const result = response;
    
    console.log('sssss',result)
    return result.data.data;
  } catch (error) {
    console.error('获取用户信息失败:', error);
    throw error;
  }
};

/**
 * 获取卖家商品列表
 * @param {string} sellerId 卖家ID
 * @param {number} page 页码（从1开始）
 * @param {number} size 每页数量
 * @returns {Promise} 返回商品列表数据
 */
export const getSellerProducts = async (sellerId, page = 1, size = 6) => {
  // 默认每页6个商品
  try {
    const response = await ax1.get(`/api-8084/commodity/list/${sellerId}?page=${page}&size=${size}`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    const result = response.data;
    
    if (result.success) {
      return {
        products: result.data.map(item => ({
          id: item.commodityId,
          title: item.commodityName,
          price: item.currentPrice,
          imageUrl: item.mainImageUrl || '/public/测试图片.jpg',
          status: item.commodityStatus,
          condition: item.newness || '成色良好',
          description: item.commodityDescription
        })),
        total: result.total || result.data.length
      };
    } else {
      throw new Error(result.message || '获取商品列表失败');
    }
  } catch (error) {
    console.error('获取卖家商品列表失败:', error);
    throw error;
  }
};
/***  
 * 获取热门推荐的商品 
 * @param {string} commodity_name 商品名称
 * @param {string} commodity_description 商品描述
 * @param {int}category_id 商品分类id
 * @param {list[int]} tags_id 标签列表
 * @param {string} current_price 现在的商品价格，前端要转换为float类型
 * @param {string} commodity_status 商品状态
 * @param {string} main_image_url 图片主图链接
 * @param {list[string]} image_list 图片列表信息
 * @param {int} quantity 商品数量
 * @param {string} newness 商品新旧程度
 * @param {string} commodity_id 商品id
 * @param {dateTime} created_at 商品创建时间
 * @param {dateTime} updated_at 商品更新时间
 * @param {string} user_name
 * @param {string} avatar_url
 * */
export const get_commodities_recommendation = async (user_id) => {
  if (!user_id) {
    return [];
  }
  try {
    const response = await instance.get(`/api/v1/commodities/recommendation/${user_id}`);
    if (response.data) {
      return response.data;
    } else {
      return [];
    }
  } catch (error) {
    console.error('获取推荐商品失败:', error);
    return [];
  }
};