-- 第一个是固定脚本名
local key = KEYS[1]
local updateCount = 0

for i = 1, #ARGV, 2 do

    local field = ARGV[i]
    local newVal = ARGV[i + 1]
    -- map的value 如果是obj 需要对field做转义处理，否则会多加一个引号
    local escapeField = string.gsub(field, '\"', '')
    local currentVal = redis.call('HGET', key, escapeField)
    if currentVal ~= newVal then
        redis.call('HSET', key, escapeField, newVal)
        updateCount = updateCount + 1
    end
end

return updateCount
