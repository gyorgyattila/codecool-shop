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
import com.codecool.shop.util.NetworkUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import com.codecool.shop.model.Order;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

//        Map params = new HashMap<>();

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        List<ProductCategory> categories = productCategoryDataStore.getAll();
        List<Supplier> suppliers = supplierDataStore.getAll();
        HttpSession session = NetworkUtils.getHTTPSession(req);
        Order order = (Order) session.getAttribute("Order");

        String category = req.getParameter("category");
        String supplier = req.getParameter("supplier");
        if (category == null && supplier == null){
            context.setVariable("products", productDataStore.getAll());
        }
        else if (category == null){
            context.setVariable("products", productDataStore.getBy(supplierDataStore.find(supplier)));
        }
        else if (supplier == null){
            System.out.println(category);
            ProductCategory productCategory = productCategoryDataStore.find(category);
            context.setVariable("products", productDataStore.getBy(productCategory));
        }
        else {
            context.setVariable("products", productDataStore.getBy(supplierDataStore.find(supplier), productCategoryDataStore.find(category)));
        }

        context.setVariable("shoppingCart", order);
        context.setVariable("categories", categories);
        context.setVariable("suppliers", suppliers);
        context.setVariable("recipient", "World");

        engine.process("product/index.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        HttpSession session = NetworkUtils.getHTTPSession(req);
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
