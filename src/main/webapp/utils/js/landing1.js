const slides = document.querySelectorAll(".slide");
const indicadores = document.querySelectorAll(".indicador");
const anterior = document.querySelector(".carrosselSeta.anterior");
const proximo = document.querySelector(".carrosselSeta.proximo");

let slideAtual = 0;
let intervalo;

function mostrarSlide(indice) {
    slides[slideAtual].classList.remove("ativo");
    indicadores[slideAtual].classList.remove("ativo");

    slideAtual = (indice + slides.length) % slides.length;

    slides[slideAtual].classList.add("ativo");
    indicadores[slideAtual].classList.add("ativo");
}

function proximoSlide() {
    mostrarSlide(slideAtual + 1);
}

function slideAnterior() {
    mostrarSlide(slideAtual - 1);
}

function iniciarCarrossel() {
    clearInterval(intervalo);
    intervalo = setInterval(proximoSlide, 5000);
}

proximo.addEventListener("click", () => {
    proximoSlide();
    iniciarCarrossel();
});

anterior.addEventListener("click", () => {
    slideAnterior();
    iniciarCarrossel();
});

indicadores.forEach((indicador, indice) => {
    indicador.addEventListener("click", () => {
        mostrarSlide(indice);
        iniciarCarrossel();
    });
});

iniciarCarrossel();