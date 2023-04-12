package com.xqf.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xqf.srb.core.dao.DictMapper;
import com.xqf.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xqf
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<ExcelDictDTO> list = new ArrayList();
    private DictMapper dictMapper;

    /**
     * 注入dictmapper
     * @param baseMapper
     */
    public ExcelDictDTOListener(DictMapper baseMapper) {
        dictMapper=baseMapper;
    }

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext analysisContext) {
        log.info("读取到的数据"+data);

        list.add(data);
        if (list.size()>=BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());
        dictMapper.insertBatch(list);  //批量插入
        log.info("存储数据库成功！");
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }
}
