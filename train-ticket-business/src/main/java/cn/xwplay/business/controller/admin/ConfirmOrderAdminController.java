package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.ConfirmOrderDoReq;
import cn.xwplay.business.req.ConfirmOrderQueryReq;
import cn.xwplay.business.resp.ConfirmOrderQueryResp;
import cn.xwplay.business.service.ConfirmOrderService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    private final ConfirmOrderService confirmOrderService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq req) {
        PageResp<ConfirmOrderQueryResp> list = confirmOrderService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }

}
