package com.wil.controller;

import com.github.pagehelper.PageInfo;
import com.wil.entity.Course;
import com.wil.entity.Major;
import com.wil.entity.Paper;
import com.wil.entity.PaperForm;
import com.wil.service.PaperService;
import com.wil.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by wil on 2018/5/11.
 */
@Controller
@RequestMapping("/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @GetMapping
    public String list(@RequestParam(name = "p", required = false, defaultValue = "1") Integer pageNo,
                       Model model, HttpSession session) {
        Integer teacherId = (Integer) session.getAttribute("teacherId");
        PageInfo<Paper> pageInfo = paperService.pageForPaperList(teacherId, pageNo);
        model.addAttribute("page", pageInfo);
        return "paper/list";
    }

    @GetMapping("/show/{id:\\d+}")
    public String show(@PathVariable Integer id, Model model) {
        Paper paper = paperService.findById(id);
        Course course = paperService.findCourseById(paper.getCourseId());
        Major major = paperService.findMajorById(paper.getMajorId());
        model.addAttribute("paper", paper);
        model.addAttribute("course", course);
        model.addAttribute("major", major);
        return "paper/show";
    }

    @GetMapping("/newPaperForm")
    public String addPaperForm() {
        return "paper/newPaperForm";
    }

    /**
     * 添加试卷组成模版
     * @param paperForm
     * @return
     */
    @PostMapping("/newPaperForm")
    public String addPaperForm(PaperForm paperForm) {
        paperService.newPaperForm(paperForm);
        return "redirect:/paper/newPaper/" + paperForm.getId();
    }

    /**
     * 添加试卷 组卷
     * @param model
     * @param id (paperForm的id,默认模版为1)
     * @param session
     * @return
     */
    @GetMapping("/newPaper/{id:\\d+}")
    public String add(Model model, @PathVariable Integer id, HttpSession session) {
        Integer teacherId = (Integer) session.getAttribute("teacherId");
        List<Course> courseList = paperService.findCourseListByTeacherId(teacherId);
        model.addAttribute("courseList", courseList);
        return "paper/newPaper";
    }

    @PostMapping("/newPaper/{paperFormId:\\d+}")
    public String add(Paper paper,@PathVariable("paperFormId") Integer paperFormId, String major) {
        paper.setPaperFormId(paperFormId);
        Major majorObj = paperService.findMajorByMajorName(major);
        paper.setMajorId(majorObj.getId());
        paperService.newPaper(paper);

        return "redirect:/paper/show/" + paper.getId();
    }

    /**
     * 展示所有试卷模版列表
     * @param model
     * @return
     */
    @GetMapping("/showPaperForm")
    public String showPaperForm(Model model) {
        List<PaperForm> formList = paperService.findAllPaperForm();
        model.addAttribute("formList", formList);
        return "paper/showPaperForm";
    }

    /**
     * Ajax删除模版不跳转页面
     * @param id
     * @return
     */
    @GetMapping("/delPaperForm/{id:\\d+}")
    @ResponseBody
    public AjaxResult delPaperForm(@PathVariable Integer id) {
        try {
            paperService.delPaperFormById(id);
            return new AjaxResult().success();
        } catch (Exception e) {
            return new AjaxResult().error(e.getMessage());
        }

    }




}
