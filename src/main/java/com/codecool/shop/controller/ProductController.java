package com.codecool.shop.controller;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import com.codecool.shop.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

//        Map params = new HashMap<>();

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        context.setVariable("recipient", "World");
        List<ProductCategory> categories = productCategoryDataStore.getAll();
        context.setVariable("categories", categories);
        String category = req.getParameter("category");
        if (category != null) {
            context.setVariable("products", productDataStore.getBy(productCategoryDataStore.find(category)));
        }
        List<Supplier> suppliers = supplierDataStore.getAll();
        context.setVariable("suppliers", suppliers);
        String supplier = req.getParameter("supplier");
        if (supplier != null) {
            context.setVariable("products", productDataStore.getBy(supplierDataStore.find(supplier)));
        }
        if (req.getSession(false) == null  ){
            HttpSession session = req.getSession(true);
            session.setAttribute("Order", new Order());
        }
        HttpSession session = req.getSession(true);
        Order order = (Order) session.getAttribute("Order");
        context.setVariable("shoppingCart", order);

        engine.process("product/index.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session;
        int id = Integer.parseInt(req.getParameter("id"));
        if (req.getSession(false) == null  ){
            session = req.getSession(true);
            session.setAttribute("Order", new Order());
        } else {
            session = req.getSession();
        }
        Order order = (Order) session.getAttribute("Order");
        String process = req.getParameter("process");
        String json = "";
        switch (process) {
            case "add":
                order.addProduct(id);
                break;
            case "remove":
                order.removeProduct(id);
                json = order.getCartItems().toJSONString();
                break;
            case "increment":
                order.addProduct(id);
                json = order.getProductQuantity(id).toJSONString();
                break;
            case "decrement":
                order.decrementQuantityOfProduct(id);
                json = order.getProductQuantity(id).toJSONString();
                break;
            case "openCart":
                json = order.getCartItems().toJSONString();
                break;
        }
        session.setAttribute("Order", order);
        resp.getWriter().write(json);

    }
}
