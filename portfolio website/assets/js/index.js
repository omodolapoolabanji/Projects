const scrollUp = document.querySelector("#scroll-up"); 
scrollUp.addEventListener("click", ()=> {
    window.scrollTo({
        top:0,
        left:0,
        behavior: "smooth",

    });
});
const burgerMenu = document.querySelector("#burger-menu");
const navUl = document.querySelector("nav ul");
const nav = document.querySelector("nav");
const navAnchor = document.querySelectorAll(".nav-link");

burgerMenu.addEventListener("click", ()=>{
    navUl.classList.toggle("show"); 
});

navAnchor.forEach((link)=>{
    link.addEventListener("click", ()=>{
        navUl.classList.remove("show");
    });
}); 

