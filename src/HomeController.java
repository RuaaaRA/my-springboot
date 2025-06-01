@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "Hello, World!";
    }
}
